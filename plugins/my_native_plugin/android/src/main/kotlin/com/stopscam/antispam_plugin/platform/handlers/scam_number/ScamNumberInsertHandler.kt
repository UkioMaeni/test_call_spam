package com.stopscam.antispam_plugin.platform.handlers.scam_number

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import android.util.Log
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.platform.handlers.common.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScamNumberInsertHandler(
    private val scope: CoroutineScope
) : Handler {

    override val callMethod : String = CallMethods.CUSTOM_NUMBER_INSERT;

    override fun handler(context: Context, call: MethodCall, result: MethodChannel.Result){
        val number = call.argument<Map<String, String>>("number")
        if(number==null){
            result.error(false)
        }

        scope.launch(Dispatchers.IO) {
            dao.insertCustomTrans(entities[0])
            withContext(Dispatchers.Main) { result.success(true) }
        }
        result.success(map)
    }
}