package com.stopscam.antispam_plugin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stopscam.antispam_plugin.data.local.entity.SpamCustomNumber

@Dao
interface  SpamCustomNumberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun spamCustomNumberInsert(number: SpamCustomNumber)

    @Query("""
        SELECT EXISTS(
          SELECT 1 FROM SpamCustomNumber
          WHERE number IN (:numbers)
          LIMIT 1
        )
    """)
    suspend fun spamCustomNumberIsSpamFromListNumbers(numbers: List<String>): Boolean

    @Query("""
        UPDATE SpamCustomNumber
          SET setBlocked = 0
        WHERE number = :phoneNumber
    """)
    suspend fun spamCustomNumberDelete(phoneNumber: String)

    @Query("SELECT * FROM SpamCustomNumber WHERE setBlocked = 1")
    suspend fun spamCustomNumberGetAll(): List<SpamCustomNumber>

    @Query("SELECT EXISTS(SELECT 1 FROM SpamCustomNumber WHERE number = :phoneNumber)")
    suspend fun isInSpamCustomNumber(phoneNumber: String): Boolean

    @Query("SELECT description FROM SpamCustomNumber WHERE number = :number LIMIT 1")
    suspend fun spamCustomNumberFindDescription(number: String): String?
}