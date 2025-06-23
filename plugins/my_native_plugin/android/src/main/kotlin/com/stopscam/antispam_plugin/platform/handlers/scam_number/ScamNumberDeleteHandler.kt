package com.stopscam.antispam_plugin.platform.handlers.scam_number

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.platform.handlers.common.Handler

class ScamNumberDeleteHandler : Handler {


    override val callMethod : String = CallMethods.CUSTOM_NUMBER_DELETE;

    override fun handler(call: MethodCall, result: MethodChannel.Result){

        result.success(null)
    }
}