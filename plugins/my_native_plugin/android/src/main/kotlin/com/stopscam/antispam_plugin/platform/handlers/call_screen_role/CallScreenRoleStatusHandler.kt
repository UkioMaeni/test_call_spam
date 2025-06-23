package com.stopscam.antispam_plugin.platform.handlers.call_screen_role

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import com.stopscam.antispam_plugin.domain.usecase.CallScreenRoleCase
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.platform.handlers.common.Handler

class CallScreenRoleStatusHandler : Handler {

    override val callMethod : String = CallMethods.CALL_SCREEN_ROLE_STATUS;

    override fun handler(call: MethodCall, result: MethodChannel.Result){
        val granted = CallScreenRoleCase.isGranted()
        result.success(granted)
    }
}