package com.stopscam.antispam_plugin.data.local.dao

import androidx.room.Dao
import androidx.room.Transaction
import com.stopscam.antispam_plugin.data.local.entity.AllowNumber
import com.stopscam.antispam_plugin.data.local.entity.SpamCustomNumber

@Dao
interface CustomNumberOperations{
    @Transaction
    suspend fun spamCustomNumberInsertProtect(number: SpamCustomNumber){
        if(allowNumberDao().isInAllowNumber(number.number)){
            allowNumberDao().allowNumberDelete(number.number)
        }
        spamCustomNumberDao().spamCustomNumberInsert(number)
    }
    @Transaction
    suspend fun allowNumberInsertProtect(number: AllowNumber){
        if(spamCustomNumberDao().isInSpamCustomNumber(number.number)){
            spamCustomNumberDao().spamCustomNumberDelete(number.number)
        }
        allowNumberDao().allowNumberInsert(number)
    }
    fun allowNumberDao(): AllowNumberDao         // уже существующий DAO с deleteAllow/isInAllow
    fun spamCustomNumberDao(): SpamCustomNumberDao
}