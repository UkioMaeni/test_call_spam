package com.yourcompany.my_native_plugin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SpamDao {
    ///работа с основной базой базой
    @Query("SELECT EXISTS(SELECT * FROM SpamNumber WHERE number = :phoneNumber)")
    suspend fun isSpam(phoneNumber: String): Boolean
    @Query("""
        SELECT EXISTS(
          SELECT 1 FROM SpamNumber
          WHERE number IN (:numbers)
          LIMIT 1
        )
    """)
    suspend fun isSpamInList(numbers: List<String>): Boolean
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(numbers: List<SpamNumber>)
    @Query("SELECT COUNT(*) FROM SpamNumber")
    suspend fun getSpamNumberCount(): Int
    @Query("DELETE FROM SpamNumber")
    suspend fun clearAll()
    @Query("SELECT MAX(CAST(number AS INTEGER)) FROM SpamNumber")
    suspend fun getMaxServerId(): Int?
    //кастомные номера
    @Query("SELECT EXISTS(SELECT * FROM SpamCustomNumber WHERE number = :phoneNumber)")
    suspend fun isSpamCustomNumber(phoneNumber: String): Boolean
    @Query("""
        SELECT EXISTS(
          SELECT 1 FROM SpamCustomNumber
          WHERE number IN (:numbers)
          LIMIT 1
        )
    """)
    suspend fun isSpamCustomNumberInList(numbers: List<String>): Boolean
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllCustomNumber(vararg numbers: SpamCustomNumber)

    @Query("SELECT * FROM SpamCustomNumber")
    suspend fun getAllCustomNumbers(): List<SpamCustomNumber>

    @Query("SELECT * FROM SpamCustomNumber WHERE number = :phoneNumber LIMIT 1")
    suspend fun getByNumberFromCustomNumbers(phoneNumber: String): SpamCustomNumber?

    @Query("DELETE FROM SpamCustomNumber")
    suspend fun deleteAllCustomNumbers()

    @Query("DELETE FROM SpamCustomNumber WHERE number = :phoneNumber")
    suspend fun deleteCustomNumbersByNumber(phoneNumber: String)

    //общие запросы
    @Query("""
    SELECT EXISTS (
        SELECT 1 FROM SpamNumber      WHERE number IN (:numbers)
        UNION ALL
        SELECT 1 FROM SpamCustomNumber WHERE number IN (:numbers)
        LIMIT 1
    )
    """)
    suspend fun isSpamAllTables(numbers: List<String>): Boolean
}
