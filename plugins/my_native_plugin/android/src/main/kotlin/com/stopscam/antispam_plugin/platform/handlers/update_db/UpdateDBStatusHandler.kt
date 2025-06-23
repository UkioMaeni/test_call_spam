package com.stopscam.antispam_plugin.platform.handlers.allow_number

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.stopscam.antispam_plugin.domain.usecase.DbCase
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.platform.handlers.common.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateDBStatusHandler(
    private val scope: CoroutineScope
) : Handler {


    override val callMethod : String = CallMethods.UPDATE_DB_STATUS;

    override fun handler(call: MethodCall, result: MethodChannel.Result){
        scope.launch{
            withContext(Dispatchers.IO) {
                val isRunning =  DbCase.dbUpdaterIsRunning()
                result.success(isRunning)
            }
        }
    }
}