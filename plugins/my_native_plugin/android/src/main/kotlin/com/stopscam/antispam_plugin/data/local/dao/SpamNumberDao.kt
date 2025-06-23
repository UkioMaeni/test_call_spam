package com.stopscam.antispam_plugin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stopscam.antispam_plugin.data.local.entity.SpamCustomNumber
import com.stopscam.antispam_plugin.data.local.entity.SpamNumber

@Dao
interface  SpamNumberDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun spamNumberInsertAll(numbers: List<SpamNumber>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun spamNumberInsert(number: SpamNumber)

    @Query("SELECT description FROM SpamNumber WHERE number = :number LIMIT 1")
    suspend fun spamNumberFindDescription(number: String): String?
}