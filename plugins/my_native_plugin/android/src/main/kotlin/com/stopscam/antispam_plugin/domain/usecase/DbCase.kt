package com.stopscam.antispam_plugin.domain.usecase

import android.os.Build
import android.util.Log
import com.stopscam.antispam_plugin.data.local.entity.AllowNumber
import com.stopscam.antispam_plugin.data.local.entity.SpamCustomNumber
import com.stopscam.antispam_plugin.domain.gateway.CallScreenGateway
import com.stopscam.antispam_plugin.platform.ServiceLocator

object DbCase{

    suspend fun insertCustomNumber(number:SpamCustomNumber): Boolean{
        try {
            ServiceLocator.database.customNumberOperations().spamCustomNumberInsertProtect(number)
            return true;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return false;
        }
   }
    suspend fun insertAllowNumber(number:AllowNumber): Boolean{
        try {
            ServiceLocator.database.customNumberOperations().allowNumberInsertProtect(number)
            return true;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return false;
        }
    }
    suspend fun selectCustomNumber(number:AllowNumber):  List<SpamCustomNumber>{
        try {
             val result =  ServiceLocator.database.spamCustomNumberDao().spamCustomNumberGetAll()
            return result;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return listOf();
        }
    }
    suspend fun selectAllowNumber(number:AllowNumber): Boolean{
        try {
            ServiceLocator.database.allowNumberDao().allowNumberDelete(number)
            return true;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return false;
        }
    }
}

 