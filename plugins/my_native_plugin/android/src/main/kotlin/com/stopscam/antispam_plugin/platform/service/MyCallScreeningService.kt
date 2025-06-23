package com.yourcompany.my_native_plugin

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import com.stopscam.antispam_plugin.data.local.db.AppDatabase
import com.stopscam.antispam_plugin.data.local.entity.SpamCustomNumber
import com.stopscam.antispam_plugin.data.local.entity.SpamNumber
import com.stopscam.antispam_plugin.data.local.prefs.SpamPrefs
import com.stopscam.antispam_plugin.platform.ServiceLocator
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.N)
class MyCallScreeningService :
    CallScreeningService(),
    CoroutineScope by MainScope() {

    private val spamNumberDao by lazy {
        AppDatabase.getInstance(applicationContext).spamNumberDao()
    }
    private val spamCustomNumberDao by lazy {
        AppDatabase.getInstance(applicationContext).spamCustomNumberDao()
    }

    private val allowNumberDao by lazy {
        AppDatabase.getInstance(applicationContext).allowNumberDao()
    }

    private val customNumberOperations by lazy {
        AppDatabase.getInstance(applicationContext).customNumberOperations()
    }

    override fun onDestroy() {
        cancel()            // закрываем все запущенные корутины
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) return
        val handle = callDetails.handle;
        if(handle == null || handle.scheme != "tel"){
            Log.d("handle", "null handle")
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }
        val phone = callDetails.handle.schemeSpecificPart
        
        Log.d("MyCS", "Incoming: $phone")
        val numbers: List<String> = if (phone.startsWith("+1")) {
            val local = phone.removePrefix("+1")                 // "2345678900"
            listOf(phone, local)                                 // ["+12345678900", "2345678900"]
        } else {
            listOf(phone)                                        //// ["2345678900"]
        }
        launch(Dispatchers.IO) {
            // 1. HTTP-лог (IO-поток)
            sendHttpLog(phone)
            // 2. Если фильтр выключен — сразу разрешаем
            Log.d("isBlockingEnabled", ServiceLocator.prefs.isBlockingEnabled().toString())
            if (!ServiceLocator.prefs.isBlockingEnabled()) {
                withContext(Dispatchers.Main) {
                    respondToCall(callDetails, CallResponse.Builder().build())
                }
                return@launch
            }
            var isAllow = allowNumberDao.isInAllowNumberFromList(numbers)
            if(isAllow){
                withContext(Dispatchers.Main) {
                    respondToCall(callDetails, CallResponse.Builder().build())
                }
                return@launch
            }
            // 3. Проверка БД
            var isSpam = customNumberOperations.isSpamAllTables(numbers)

            var isSpamOnRemote = false;

            if(!isSpam){
                var remoteBlockAsync: Deferred<Pair<Boolean,String>> = async {
                    val res: Pair<Boolean, String>? = withTimeoutOrNull(3_000) {
                       isRemoteSpam(phone)
                    }
                    res ?: (false to "")        // тай-аут или ошибка сети → не блокируем
                }
                val (remoteBlock, descr) = remoteBlockAsync.await()
                if(remoteBlock){
                    isSpamOnRemote=true;
                    val scumNumber = SpamNumber(
                        number      = phone,
                        description = descr ?: "not",
                        serverId = 0
                    )
                    spamNumberDao.spamNumberInsert(scumNumber)
                }
                
            }
            
            if(isSpamOnRemote){
              isSpam =  isSpamOnRemote; 
            }

            Log.d("isSpamAllTables", isSpam.toString())
            // 4. Ответ системе
            withContext(Dispatchers.Main) {
                respondToCall(
                    callDetails,
                    CallResponse.Builder()
                        .setDisallowCall(isSpam)
                        .setRejectCall(isSpam)
                        .build()
                )
            }
        }
    }

    private fun sendHttpLog(phone: String) {
        runCatching {
            val url = URL("https://call.stopscam.ai/api/v1/numberRouter")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                outputStream.use { it.write("""{"phone":"$phone"}""".toByteArray()) }
                Log.d("HTTP", "Response code: $responseCode")
                inputStream.close()
                disconnect()
            }
        }.onFailure { Log.e("HTTP", "Error: ${it.message}") }
    }

    private suspend fun isRemoteSpam(number: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val json      = """{"number":"$number"}"""
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body      = json.toRequestBody(mediaType)

        val req = Request.Builder()
            .url("https://call.stopscam.ai/api/v1/numberRouter/check")
            .post(body)
            .build()

        runCatching {
            OkHttpClient().newCall(req).execute().use { r ->
                if (!r.isSuccessful) return@runCatching (false to "not")

                val obj = JSONObject(r.body!!.string())
                val blocked = obj.optBoolean("block", false)
                val descr   = obj.optString("description", "not")
                blocked to descr
            }
        }.getOrDefault(false to "not")
    }
}
