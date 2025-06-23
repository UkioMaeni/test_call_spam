package com.stopscam.antispam_plugin.platform.handlers.allow_number

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import com.stopscam.antispam_plugin.domain.usecase.CallBlockCase
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.platform.handlers.common.Handler

class CallBlockStatusHandler : Handler {


    override val callMethod : String = CallMethods.CALL_BLOCK_STATUS;

    override fun handler(call: MethodCall, result: MethodChannel.Result){
        val isBlockedNow =  CallBlockCase.isBlockingEnabled();
        result.success(isBlockedNow)
    }
}