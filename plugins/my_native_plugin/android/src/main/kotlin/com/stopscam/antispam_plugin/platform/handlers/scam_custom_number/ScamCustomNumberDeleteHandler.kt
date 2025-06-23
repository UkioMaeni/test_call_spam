package com.stopscam.antispam_plugin.platform.handlers.scam_custom_number

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import com.stopscam.antispam_plugin.data.local.entity.SpamCustomNumber
import com.stopscam.antispam_plugin.domain.usecase.DbCase
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.platform.handlers.common.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScamCustomNumberDeleteHandler(
    private val scope: CoroutineScope
) : Handler {


    override val callMethod : String = CallMethods.CUSTOM_NUMBER_DELETE;

    override fun handler(call: MethodCall, result: MethodChannel.Result){
        val number = call.argument<String>("number")
        if(number==null){
            result.success(false)
            return;
        }
        scope.launch{
            var deleted: Boolean;
            withContext(Dispatchers.IO) {
                deleted = DbCase.deleteCustomNumber(number);
            }
            result.success(deleted)
        }
    }
}