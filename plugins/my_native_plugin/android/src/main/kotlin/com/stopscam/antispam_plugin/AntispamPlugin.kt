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
import com.stopscam.antispam_plugin.data.local.prefs.SpamPrefs
import com.stopscam.antispam_plugin.platform.ServiceLocator
import com.stopscam.antispam_plugin.platform.gateway.CallScreenGatewayImpl
import com.stopscam.antispam_plugin.platform.handlers.allow_number.AllowNumberDeleteHandler
import com.stopscam.antispam_plugin.platform.handlers.allow_number.AllowNumberGetAllHandler
import com.stopscam.antispam_plugin.platform.handlers.allow_number.AllowNumberInsertHandler
import com.stopscam.antispam_plugin.platform.handlers.allow_number.CallBlockSetStatusHandler
import com.stopscam.antispam_plugin.platform.handlers.allow_number.CallBlockStatusHandler
import com.stopscam.antispam_plugin.platform.handlers.allow_number.PhoneLogCallsHandler
import com.stopscam.antispam_plugin.platform.handlers.allow_number.PhoneLogSMSHandler
import com.stopscam.antispam_plugin.platform.handlers.allow_number.UpdateDBStartHandler
import com.stopscam.antispam_plugin.platform.handlers.allow_number.UpdateDBStatusHandler
import com.stopscam.antispam_plugin.platform.handlers.call_screen_role.CallScreenRoleRequestHandler
import com.stopscam.antispam_plugin.platform.handlers.call_screen_role.CallScreenRoleStatusHandler
import com.stopscam.antispam_plugin.platform.handlers.common.Handler
import com.stopscam.antispam_plugin.platform.handlers.scam_custom_number.ScamCustomNumberDeleteHandler
import com.stopscam.antispam_plugin.platform.handlers.scam_custom_number.ScamCustomNumberGetAllHandler
import com.stopscam.antispam_plugin.platform.handlers.scam_custom_number.ScamCustomNumberInsertHandler
import com.stopscam.antispam_plugin.platform.service.DbStreamWorker

private const val CHANNEL = "my_native_plugin"
private const val REQUEST_CODE_ROLE = 321

class AntispamPlugin : FlutterPlugin,
    MethodChannel.MethodCallHandler,
    ActivityAware,
    PluginRegistry.ActivityResultListener,
    CoroutineScope by MainScope() {

    private lateinit var channel: MethodChannel

    val handlers : List<Handler> = listOf(

        AllowNumberDeleteHandler(this),
        AllowNumberGetAllHandler(this),
        AllowNumberInsertHandler(this),

        ScamCustomNumberDeleteHandler(this),
        ScamCustomNumberGetAllHandler(this),
        ScamCustomNumberInsertHandler(this),

        CallBlockSetStatusHandler(),
        CallBlockStatusHandler(),

        CallScreenRoleRequestHandler(this),
        CallScreenRoleStatusHandler(),

        PhoneLogCallsHandler(this),
        PhoneLogSMSHandler(this),

        UpdateDBStartHandler(this),
        UpdateDBStatusHandler(this),

    )

    // Room
    private lateinit var db: AppDatabase
    @VisibleForTesting internal lateinit var dao: SpamDao   // тестам будет проще

    /* ---------- FlutterPlugin ---------- */
    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {

        val appContext = binding.applicationContext
        ServiceLocator.init(appContext);

        channel = MethodChannel(binding.binaryMessenger, CHANNEL)
        channel.setMethodCallHandler(this)

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
    override fun onDetachedFromActivity() {
        ServiceLocator.activity=null
    }
    override fun onReattachedToActivityForConfigChanges(b: ActivityPluginBinding) =
        onAttachedToActivity(b)
    override fun onDetachedFromActivityForConfigChanges() {}

    /* ---------- MethodChannel ---------- */
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {

        val handler = handlers.firstOrNull { it.callMethod == call.method }
        if (handler != null) {
            handler.handler(call, result)
        } else {
            result.notImplemented()
        }
//        when (call.method) {
//            "isCallScreeningRoleHeld" -> isRoleHeld(result)///
//            "requestCallScreeningRole" -> requestRole(result)///
//
//            "countSpamNumbers"        -> countSpamNumbers(call, result)
//            "insertSpamNumbers"        -> insertSpamNumbers(call, result)
//            "clearSpamDatabase"        -> clearSpamDatabase(result)
//            "getSpamNumberCount"      ->    getSpamNumberCount(result)
//
//            "insertSpamCustomNumbers"  -> insertSpamCustomNumbers(call, result)///
//            "selectDatabaseCustomNumbers"-> selectDatabaseCustomNumbers(result)///
//            "deleteDatabaseCustomNumbers"-> deleteDatabaseCustomNumbers(result)//-
//            "deleteCustomNumbersByNumber"-> deleteCustomNumbersByNumber(call, result)///
//
//            "setCallBlockingEnabled"   -> setCallBlockingEnabled(call, result)///
//            "getCallBlockingEnabled"   -> getCallBlockingEnabled(call, result)///
//            "isCallBlockingEnabled"    -> result.success(SpamPrefs.isBlockingEnabled(appContext))///
//
//            "updateDb"                      -> updateDb(call, result)///
//            "updateDbISRunning"              -> updateDbISRunning(call, result)///
//            "getCallLog"                -> getCallLog(call, result)///
//
//            //проверки блок
//            "getDescriptionFromAllScam"          ->getDescriptionFromAllScam(call, result)//
//            "insertAllow"          ->insertAllow(call, result)///
//            "deleteAllow"          ->deleteAllow(call, result)///
//            "getAllow"          ->getAllow(result)///
//            else                       -> result.notImplemented()
//        }
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

}
