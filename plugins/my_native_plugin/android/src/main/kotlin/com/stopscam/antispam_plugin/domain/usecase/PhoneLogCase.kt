package com.stopscam.antispam_plugin.domain.usecase

import android.util.Log
import com.stopscam.antispam_plugin.data.local.entity.AllowNumber
import com.stopscam.antispam_plugin.data.local.entity.SpamCustomNumber
import com.stopscam.antispam_plugin.platform.ServiceLocator

object PhoneLogCase {
    suspend fun getCallsLog(): Any? {
        try {
            val result =  ServiceLocator.phoneLogGateway.getCallsLog()
            return result;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return null;
        }
    }
    suspend fun getSMSLog(): Any? {
        try {
            val result =  ServiceLocator.phoneLogGateway.getSMSLog()
            return result;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return null;
        }
    }
}