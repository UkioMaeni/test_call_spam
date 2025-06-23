package com.stopscam.antispam_plugin.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.stopscam.antispam_plugin.domain.gateway.CallScreenGateway
import com.stopscam.antispam_plugin.platform.ServiceLocator

object CallBlockCase{
   fun isBlockingEnabled(): Boolean{
        val isBlockingEnabled = ServiceLocator.prefs.isBlockingEnabled();
        return isBlockingEnabled;
   }
    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
     fun setBlockingEnabled(flag: Boolean): Boolean{
        ServiceLocator.prefs.setBlockingEnabled(flag);
        return  isBlockingEnabled();
    }
}

 