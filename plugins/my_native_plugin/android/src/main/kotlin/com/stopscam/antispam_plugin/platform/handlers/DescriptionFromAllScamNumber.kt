package com.stopscam.antispam_plugin.platform.handlers

import android.content.Context
import com.stopscam.antispam_plugin.domain.usecase.DbCase
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.platform.handlers.common.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DescriptionFromAllScamNumber(
    private val scope: CoroutineScope
) : Handler {


    override val callMethod : String = CallMethods.DESCRIPTION_SCAM;

    override fun handler(context: Context, call: MethodCall, result: MethodChannel.Result){

        val number = call.argument<String>("number")

        if (number.isNullOrBlank()) {          // аргумент не пришёл
            result.success(null)
            return
        }

        scope.launch{
            withContext(Dispatchers.IO) {
                val isRunning =  DbCase.findDescription(number)
                result.success(isRunning)
            }
        }
    }


}