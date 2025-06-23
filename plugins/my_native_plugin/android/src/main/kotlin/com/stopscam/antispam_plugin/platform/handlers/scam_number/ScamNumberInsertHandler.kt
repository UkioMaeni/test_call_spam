package com.stopscam.antispam_plugin.platform.handlers.scam_number

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
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

    override fun handler(call: MethodCall, result: MethodChannel.Result){

        result.success(null)
    }
}