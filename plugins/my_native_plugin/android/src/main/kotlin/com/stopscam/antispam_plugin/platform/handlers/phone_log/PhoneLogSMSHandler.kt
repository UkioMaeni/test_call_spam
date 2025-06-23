package com.stopscam.antispam_plugin.platform.handlers.allow_number

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import com.stopscam.antispam_plugin.domain.usecase.PhoneLogCase
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.platform.handlers.common.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhoneLogSMSHandler(
    private val scope: CoroutineScope
) : Handler {


    override val callMethod : String = CallMethods.PHONE_LOG_SMS;

    override fun handler(call: MethodCall, result: MethodChannel.Result){
        scope.launch{
            withContext(Dispatchers.IO) {
                val log =  PhoneLogCase.getSMSLog();
                result.success(log)
            }
        }
    }
}