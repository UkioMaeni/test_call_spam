package com.stopscam.antispam_plugin.domain.usecase

import android.os.Build
import com.stopscam.antispam_plugin.domain.gateway.CallScreenGateway
import com.stopscam.antispam_plugin.platform.ServiceLocator

object CallScreenRoleCase{
   fun isGranted(): Boolean{
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return false
        }
        val isGranted = ServiceLocator.callScreenRoleGateway.isGranted();
        return isGranted;
   }
    //данный метод должен отдать получена ли роль
    suspend fun requestRole(): Boolean{
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return false;
        }
        return ServiceLocator.callScreenRoleGateway.requestRole();
   }
}

 