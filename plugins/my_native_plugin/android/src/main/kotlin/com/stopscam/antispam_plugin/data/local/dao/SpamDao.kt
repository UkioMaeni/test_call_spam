package com.stopscam.antispam_plugin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCustomNumber(numbers: List<SpamCustomNumber>)

    @Query("SELECT * FROM SpamCustomNumber WHERE setBlocked = 1")
    suspend fun getAllCustomNumbers(): List<SpamCustomNumber>

    @Query("SELECT * FROM SpamCustomNumber WHERE number = :phoneNumber LIMIT 1")
    suspend fun getByNumberFromCustomNumbers(phoneNumber: String): SpamCustomNumber?

    @Query("DELETE FROM SpamCustomNumber")
    suspend fun deleteAllCustomNumbers()

    @Query("""
        UPDATE SpamCustomNumber
          SET setBlocked = 0
        WHERE number = :phoneNumber
    """)
    suspend fun deleteCustomNumbersByNumber(phoneNumber: String)

    //общие запросы
    @Query("""
    SELECT EXISTS (
        SELECT 1 FROM SpamNumber      WHERE number IN (:numbers)
        UNION ALL
        SELECT 1 FROM SpamCustomNumber WHERE number IN (:numbers) AND setBlocked = 1
        LIMIT 1
    )
    """)
    suspend fun isSpamAllTables(numbers: List<String>): Boolean

  @Query("SELECT description FROM SpamNumber WHERE number = :number LIMIT 1")
  suspend fun findDescription(number: String): String?

  @Query("SELECT description FROM SpamCustomNumber WHERE number = :number LIMIT 1")
  suspend fun findCustomDescription(number: String): String?


  @Query("SELECT EXISTS(SELECT 1 FROM AllowNumber WHERE number = :phone)")
  suspend fun isInAllow(phone: String): Boolean

  @Query("SELECT EXISTS(SELECT 1 FROM SpamCustomNumber WHERE number = :phone)")
  suspend fun isInCustom(phone: String): Boolean

  ///allow
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertAllow(numbers: AllowNumber)

  @Transaction
  suspend fun insertAllowTrans(numbers: AllowNumber){
    if(isInCustom(numbers.number)){
      deleteCustomNumbersByNumber(numbers.number)
    }
    insertAllow(numbers)
  }

  @Transaction
  suspend fun insertCustomTrans(numbers: SpamCustomNumber){
    if(isInAllow(numbers.number)){
      deleteAllow(numbers.number)
    }
    insertAllCustomNumber(listOf(numbers))
  }

  @Query("DELETE FROM AllowNumber where number = :phoneNumber")
  suspend fun deleteAllow(phoneNumber: String)

  @Query("SELECT EXISTS(SELECT 1 FROM AllowNumber WHERE number IN (:numbers))")
  suspend fun existAllow(numbers: List<String>): Boolean

  @Query("SELECT * FROM AllowNumber")
    suspend fun getAllow(): List<AllowNumber>
}
