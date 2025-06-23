package com.stopscam.antispam_plugin.platform.gateway

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.stopscam.antispam_plugin.domain.gateway.PhoneLogGateway
import com.yourcompany.my_native_plugin.CallLogService

class PhoneLogGatewayImpl(
    private val ctx: Context,
): PhoneLogGateway {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresPermission(android.Manifest.permission.READ_CALL_LOG)
    override suspend fun getCallsLog():  List<Map<String, Any?>> {
        val limit = 50
        val log   = CallLogService.getCallLog(ctx, limit)
        Log.d("getCallLog", "infa sobrana")
        return  log;
    }

    override suspend fun getSMSLog() {

    }

}