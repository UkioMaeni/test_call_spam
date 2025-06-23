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

class ScamCustomNumberGetAllHandler(
    private val scope: CoroutineScope
) : Handler {


    override val callMethod : String = CallMethods.CUSTOM_NUMBER_GET_ALL;

    override fun handler(call: MethodCall, result: MethodChannel.Result){

        scope.launch{
            var selected: List<SpamCustomNumber>;
            withContext(Dispatchers.IO) {
                selected = DbCase.selectCustomNumber();
            }
            val wire = selected.map { e ->
                mapOf(
                    "number"      to e.number,
                    "description" to e.description
                )
            }
            result.success(wire)
        }
    }
}