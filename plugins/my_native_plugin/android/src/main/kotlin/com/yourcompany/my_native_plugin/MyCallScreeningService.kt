package com.yourcompany.my_native_plugin

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class MyCallScreeningService :
    CallScreeningService(),
    CoroutineScope by MainScope() {

    private val dao by lazy {
        AppDatabase.getInstance(applicationContext).spamDao()
    }

    override fun onDestroy() {
        cancel()            // закрываем все запущенные корутины
        super.onDestroy()
    }

    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) return

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
            Log.d("isBlockingEnabled", SpamPrefs.isBlockingEnabled(applicationContext).toString())
            if (!SpamPrefs.isBlockingEnabled(applicationContext)) {
                withContext(Dispatchers.Main) {
                    respondToCall(callDetails, CallResponse.Builder().build())
                }
                return@launch
            }

            // 3. Проверка БД

            val isSpam = dao.isSpamAllTables(numbers)
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
}
