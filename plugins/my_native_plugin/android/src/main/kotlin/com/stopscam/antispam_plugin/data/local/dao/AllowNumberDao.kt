package com.stopscam.antispam_plugin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stopscam.antispam_plugin.data.local.entity.AllowNumber
import com.stopscam.antispam_plugin.data.local.entity.SpamCustomNumber

@Dao
interface  AllowNumberDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun allowNumberInsert(numbers: AllowNumber)

    @Query("DELETE FROM AllowNumber where number = :phoneNumber")
    suspend fun allowNumberDelete(phoneNumber: String)

    @Query("SELECT EXISTS(SELECT 1 FROM AllowNumber WHERE number = :phoneNumber)")
    suspend fun isInAllowNumber(phoneNumber: String): Boolean

    @Query("SELECT * FROM AllowNumber")
    suspend fun allowNumberGetAll(): List<AllowNumber>
}