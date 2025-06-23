package com.stopscam.antispam_plugin.platform.handlers.call_screen_role

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.domain.usecase.CallScreenRoleCase
import com.stopscam.antispam_plugin.platform.handlers.common.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CallScreenRoleRequestHandler(
    private val scope: CoroutineScope
) : Handler,CoroutineScope by MainScope()  {

    override val callMethod : String = CallMethods.CALL_SCREEN_ROLE_REQUEST;


    override fun handler(context: Context, call: MethodCall, result: MethodChannel.Result){
        scope.launch {
            val granted = CallScreenRoleCase.requestRole()
            result.success(granted)
        }

    }
}