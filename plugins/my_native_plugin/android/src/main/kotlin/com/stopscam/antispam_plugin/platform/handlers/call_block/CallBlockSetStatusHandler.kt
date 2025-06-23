package com.stopscam.antispam_plugin.platform.handlers.allow_number

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.stopscam.antispam_plugin.domain.usecase.CallBlockCase
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.platform.handlers.common.Handler

class CallBlockSetStatusHandler : Handler {


    override val callMethod : String = CallMethods.CALL_BLOCK_SET_STATUS;

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    override fun handler(call: MethodCall, result: MethodChannel.Result){
        val enabled = call.argument<Boolean>("enabled")
        if(enabled==null){
            result.error("INVALID_ARGUMENT", "Missing ‘number’ parameter", null)
            return;
        }
        val isBlockedNow =  CallBlockCase.setBlockingEnabled(enabled);
        result.success(isBlockedNow)
    }
}