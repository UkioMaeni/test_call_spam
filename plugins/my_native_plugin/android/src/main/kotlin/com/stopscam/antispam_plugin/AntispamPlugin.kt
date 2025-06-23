package com.yourcompany.my_native_plugin

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.VisibleForTesting
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import kotlinx.coroutines.*
import android.util.Log
import androidx.work.*
import com.stopscam.antispam_plugin.platform.ServiceLocator
import com.stopscam.antispam_plugin.platform.gateway.CallScreenGatewayImpl
import com.stopscam.antispam_plugin.platform.handlers.call_screen_role.CallScreenRoleRequestHandler

private const val CHANNEL = "my_native_plugin"
private const val REQUEST_CODE_ROLE = 321

class AntispamPlugin : FlutterPlugin,
    MethodChannel.MethodCallHandler,
    ActivityAware,
    PluginRegistry.ActivityResultListener,
    CoroutineScope by MainScope() {

    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var pendingResult: MethodChannel.Result? = null
    private lateinit var appContext: Context

    val handlers = listOf(
        CallScreenRoleRequestHandler(this),
        AnotherFeatureHandler(this),
        /* …другие handlers… */
    )

    // Room
    private lateinit var db: AppDatabase
    @VisibleForTesting internal lateinit var dao: SpamDao   // тестам будет проще

    /* ---------- FlutterPlugin ---------- */
    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {

        appContext = binding.applicationContext

        channel = MethodChannel(binding.binaryMessenger, CHANNEL)
        channel.setMethodCallHandler(this)
        ServiceLocator.init(appContext);
        // Имеем applicationContext – создаём БД
//        db  = AppDatabase.getInstance(binding.applicationContext)
//        dao = db.spamDao()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        cancel()                          // закроем все корутины
    }

    /* ---------- ActivityAware ---------- */
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        ServiceLocator.activity=binding.activity
        binding.addActivityResultListener(this)
    }
    override fun onDetachedFromActivity() { activity = null }
    override fun onReattachedToActivityForConfigChanges(b: ActivityPluginBinding) =
        onAttachedToActivity(b)
    override fun onDetachedFromActivityForConfigChanges() {}

    /* ---------- MethodChannel ---------- */
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {

        val handler = handlers.firstOrNull { it.method == call.method }
        when (call.method) {
            "isCallScreeningRoleHeld" -> isRoleHeld(result)///
            "requestCallScreeningRole" -> requestRole(result)///

            "countSpamNumbers"        -> countSpamNumbers(call, result)
            "insertSpamNumbers"        -> insertSpamNumbers(call, result)
            "clearSpamDatabase"        -> clearSpamDatabase(result)
            "getSpamNumberCount"      ->    getSpamNumberCount(result)
            
            "insertSpamCustomNumbers"  -> insertSpamCustomNumbers(call, result)//
            "selectDatabaseCustomNumbers"-> selectDatabaseCustomNumbers(result)//
            "deleteDatabaseCustomNumbers"-> deleteDatabaseCustomNumbers(result)//
            "deleteCustomNumbersByNumber"-> deleteCustomNumbersByNumber(call, result)//

            "setCallBlockingEnabled"   -> setCallBlockingEnabled(call, result)//
            "getCallBlockingEnabled"   -> getCallBlockingEnabled(call, result)//
            "isCallBlockingEnabled"    -> result.success(SpamPrefs.isBlockingEnabled(appContext))//

            "updateDb"                      -> updateDb(call, result)//
            "updateDbISRunning"              -> updateDbISRunning(call, result)//
            "getCallLog"                -> getCallLog(call, result)

            //проверки блок 
            "getDescriptionFromAllScam"          ->getDescriptionFromAllScam(call, result)//
            "insertAllow"          ->insertAllow(call, result)//
            "deleteAllow"          ->deleteAllow(call, result)//
            "getAllow"          ->getAllow(result)//
            else                       -> result.notImplemented()
        }
    }

    private fun getDescriptionFromAllScam(call: MethodCall,result: MethodChannel.Result){
        val number: String? = call.argument<String>("number")

        if (number.isNullOrBlank()) {          // аргумент не пришёл
            result.success(null)
            return
        }

        launch(Dispatchers.IO) {
            val findAnyDescription =  dao.findDescription(number) ?: dao.findCustomDescription(number);
            withContext(Dispatchers.Main) { result.success(findAnyDescription) }
        }
    }

    private fun insertSpamCustomNumbers(call: MethodCall,result: MethodChannel.Result){
        val raw = call.argument<List<Map<String, String>>>("numbers").orEmpty()

        Log.d("DatabaseDebug", "Полученные В БД: $raw")
        if (raw.isEmpty()) { result.success(true); return }

        val entities = raw.mapNotNull { m ->
            m["number"]?.let { number ->
                val desc = m["description"] ?: ""
                SpamCustomNumber(number, desc)
            }
        }
        Log.d("DatabaseDebug", "Преобразованные сущности для вставки: ${entities.joinToString()}")
        launch(Dispatchers.IO) {
            dao.insertCustomTrans(entities[0])
            withContext(Dispatchers.Main) { result.success(true) }
        }
    }

    private fun selectDatabaseCustomNumbers(result: MethodChannel.Result){
        launch(Dispatchers.IO) {
            val rows = dao.getAllCustomNumbers()
            Log.d("DatabaseDebug", "Полученные строки из БД: $rows")
            // Переводим в формат, который StandardMessageCodec понимает
            val wire = rows.map { e ->
                mapOf(
                    "number"      to e.number,
                    "description" to e.description
                )
            }
            Log.d("DatabaseDebug", "Данные в формате wire: $wire")
            withContext(Dispatchers.Main) { result.success(wire) }
        }
    }

    private fun getSpamNumberCount(result: MethodChannel.Result){
        launch(Dispatchers.IO) {
            val count = dao.getSpamNumberCount()
            withContext(Dispatchers.Main) { result.success(count) }
        }
    }
    

    private fun deleteDatabaseCustomNumbers(result: MethodChannel.Result){
        launch(Dispatchers.IO) {
            dao.deleteAllCustomNumbers()
            withContext(Dispatchers.Main) { result.success(true) }
        }
    }
    private fun deleteCustomNumbersByNumber(call: MethodCall,result: MethodChannel.Result){
        val numberToDelete = call.argument<String?>("number")
        if (numberToDelete == null) {
            Log.e("DatabaseDebug", "Номер для удаления не был предоставлен.")
            result.success(false)
            return
        }
        Log.d("DatabaseDebug", "Попытка удалить номер: $numberToDelete")
        launch(Dispatchers.IO) {
            dao.deleteCustomNumbersByNumber(numberToDelete)
            withContext(Dispatchers.Main) { 
                Log.d("DatabaseDebug", "Номер $numberToDelete успешно удален.")
                result.success(true) 
            }
        }
    }

    /** 1. Проверяем, держит ли приложение роль Call Screening */
    private fun isRoleHeld(result: MethodChannel.Result) {
        // До Android Q ролей не было — считаем «да»
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            result.success(false); return
        }

        val act = activity      // Activity может быть null, но для проверки она не нужна
        val rm = (act ?: appContext).getSystemService(RoleManager::class.java)

        val held = rm.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
                rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)

        result.success(held)
    }
    /** 2. Запрашиваем роль у пользователя (если её ещё нет) */
    private fun requestRole(result: MethodChannel.Result) {
        val act = activity ?: run {
            result.error("NO_ACTIVITY", "Plugin not attached to foreground Activity", null)
            return
        }

        // До Q — роли нет, считаем успешным
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            result.success(false); return
        }

        val rm = act.getSystemService(RoleManager::class.java)
        if (!rm.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
            result.error("UNAVAILABLE", "ROLE_CALL_SCREENING not available", null)
            return
        }
        if (rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            result.success(true); return                // уже есть
        }

        pendingResult = result
        val intent = rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        act.startActivityForResult(intent, REQUEST_CODE_ROLE)
    }

    private fun setCallBlockingEnabled(call: MethodCall, result: MethodChannel.Result) {
        val enabled = call.argument<Boolean>("enabled") ?: true
        SpamPrefs.setBlockingEnabled(appContext, enabled)
        result.success(true)
    }
    private fun getCallBlockingEnabled(call: MethodCall, result: MethodChannel.Result) {
        val isBlocked =  SpamPrefs.isBlockingEnabled(appContext)
        result.success(isBlocked)
    }

    private fun countSpamNumbers(call: MethodCall, result: MethodChannel.Result) {
        launch(Dispatchers.IO) {
            val count =  dao.getMaxServerId()
            withContext(Dispatchers.Main) { result.success(count) }
        }
    }
    

    /* ----- вставка номеров ----- */
    private fun insertSpamNumbers(call: MethodCall, result: MethodChannel.Result) {
        val raw = call.argument<List<Map<String, Any?>>>("numbers").orEmpty()
        if (raw.isEmpty()) { result.success(true); return }

        val entities = raw.mapNotNull { m ->
            val number = m["number"] as? String ?: return@mapNotNull null

            // 2) «description» может быть любой, приводим к String или default ""
            val description = m["description"] as? String ?: ""

            // 3) «serverId» может прийти как Int, Long, Double или String
            val serverId = when (val sid = m["id"]) {
                is Int    -> sid
                is Long   -> sid.toInt()
                is Double -> sid.toInt()
                is String -> sid.toIntOrNull() ?: 0
                else      -> 0
            }
            SpamNumber(
                number    = number,
                serverId  = serverId,
                description = description
            )
        }.toTypedArray()

        launch(Dispatchers.IO) {
            //dao.insertAll(*entities)
            withContext(Dispatchers.Main) { result.success(true) }
        }
    }

    /* ----- очистка базы ----- */
    private fun clearSpamDatabase(result: MethodChannel.Result) {
        launch(Dispatchers.IO) {
            dao.clearAll()
            withContext(Dispatchers.Main) { result.success(true) }
        }
    }

    /* ---------- ActivityResultListener ---------- */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode != CallScreenGatewayImpl.REQUEST_CODE_ROLE){
            ServiceLocator.callScreenRoleGateway.handleActivityResult(resultCode)
            val granted = (resultCode == Activity.RESULT_OK)
            return  granted;
        }
        return false
    }


    private fun updateDb(call: MethodCall, result: MethodChannel.Result) {


        val req = OneTimeWorkRequestBuilder<DbStreamWorker>()
                    .addTag("db_stream_worker") 
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
        WorkManager.getInstance(appContext)
                    .enqueueUniqueWork("spam-db-update",
                                       ExistingWorkPolicy.REPLACE, req)
                result.success(true)
    }
    private fun updateDbISRunning(call: MethodCall, result: MethodChannel.Result) {
        launch(Dispatchers.IO) {
            val list = WorkManager.getInstance(appContext)
            .getWorkInfosForUniqueWork("spam-db-update")   // или .getWorkInfosByTag(TAG)
            .await()
            val isRunning = list.any { it.state == WorkInfo.State.RUNNING }

            withContext(Dispatchers.Main) {
                result.success(isRunning)            
            }
                
        }
        
    }
    private fun insertAllow(call: MethodCall, result: MethodChannel.Result) {
        val raw = call.argument<Map<String, Any?>>("number")

        if (raw == null) {
            Log.e("insertAllow", "Номер для удаления не был предоставлен.")
            result.success(false)
            return
        }

         val number = raw["number"] as? String ?: ""
         val description = raw["description"] as? String ?: ""

        val allow =  AllowNumber(
                number    = number,
                description = description
            )
        launch(Dispatchers.IO) {
            dao.insertAllowTrans(allow)
            withContext(Dispatchers.Main) { 
                Log.d("DatabaseDebug", "Номер $number успешно добавлен.")
                result.success(true) 
            }
        } 
    }
    private fun deleteAllow(call: MethodCall, result: MethodChannel.Result) {
        val number = call.argument<String>("number")
        if(number==null){
            result.success(false)
            return
        }
        launch(Dispatchers.IO) {
            dao.deleteAllow(number)
            withContext(Dispatchers.Main) { result.success(true) }
        }
    }

    private fun getAllow(result: MethodChannel.Result){
        launch(Dispatchers.IO) {
            val rows = dao.getAllow()
            Log.d("DatabaseDebug", "Полученные строки из БД: $rows")
            // Переводим в формат, который StandardMessageCodec понимает
            val wire = rows.map { e ->
                mapOf(
                    "number"      to e.number,
                    "description" to e.description
                )
            }
            Log.d("DatabaseDebug", "Данные в формате wire: $wire")
            withContext(Dispatchers.Main) { result.success(wire) }
        }
    }


    private fun getCallLog(call: MethodCall, result: MethodChannel.Result) {
        val limit = call.argument<Int?>("limit") ?: 50
        val log   = CallLogService.getCallLog(appContext, limit)
        Log.d("getCallLog", "infa sobrana")
        result.success(log)
    }
}
