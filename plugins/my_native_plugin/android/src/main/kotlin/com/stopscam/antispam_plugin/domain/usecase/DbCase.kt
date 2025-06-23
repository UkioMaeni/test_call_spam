package com.stopscam.antispam_plugin.domain.usecase

import android.util.Log
import com.stopscam.antispam_plugin.data.local.entity.AllowNumber
import com.stopscam.antispam_plugin.data.local.entity.SpamCustomNumber
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
    suspend fun selectCustomNumber():  List<SpamCustomNumber>{
        try {
            val result = ServiceLocator.database.spamCustomNumberDao().spamCustomNumberGetAll()
            return result;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return listOf();
        }
    }
    suspend fun selectAllowNumber():  List<AllowNumber>{
        try {
            val result = ServiceLocator.database.allowNumberDao().allowNumberGetAll()
            return result;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return listOf();
        }
    }
    suspend fun deleteCustomNumber(number: String): Boolean{
        try {
            val result = ServiceLocator.database.spamCustomNumberDao().spamCustomNumberDelete(number)
            return true;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return false;
        }
    }
    suspend fun deleteAllowNumber(number: String):  Boolean{
        try {
            val result = ServiceLocator.database.allowNumberDao().allowNumberDelete(number)
            return true;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return false;
        }
    }

    suspend fun dbUpdaterIsRunning():  Boolean{
        try {
            val result = ServiceLocator.dbUpdaterGateway.isRunning()
            return result;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return false;
        }
    }

    suspend fun startUpdate():  Boolean{
        try {
            if(dbUpdaterIsRunning()){
                return false;
            }
            val result = ServiceLocator.dbUpdaterGateway.updateDb()
            return result;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return false;
        }
    }

    suspend fun findDescription(number:String): String? {
        try {
            val findAnyDescription = ServiceLocator.database.spamNumberDao().spamNumberFindDescription(number) ?:
                                         ServiceLocator.database.spamCustomNumberDao().spamCustomNumberFindDescription(number);
            return findAnyDescription;
        }catch (e:Exception){
            Log.e("insertCustomNumber",e.toString())
            return null;
        }
    }
}

 