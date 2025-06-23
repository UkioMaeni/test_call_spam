package com.stopscam.antispam_plugin.data.local.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.stopscam.antispam_plugin.data.local.dao.SpamDao
import com.stopscam.antispam_plugin.data.local.entity.SpamNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@Database(
    entities = [SpamNumber::class, SpamCustomNumber::class,AllowNumber::class],
    version = 6,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun spamDao(): SpamDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // миграция 1→2: включаем incremental-vacuum
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `SpamCustomNumber` (
                        `number` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        PRIMARY KEY(`number`)
                    )
                """.trimIndent())
            }
        }

        // миграция 1→2: включаем incremental-vacuum
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("PRAGMA auto_vacuum = INCREMENTAL;")
            }
        }

        // миграция 1→2: включаем incremental-vacuum
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    ALTER TABLE `SpamNumber`
                    ADD COLUMN `serverId` INTEGER NOT NULL DEFAULT 0
                    """.trimIndent())
                db.execSQL("PRAGMA auto_vacuum = INCREMENTAL;")
            }
        }
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `AllowNumber` (
                        `number` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        PRIMARY KEY(`number`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    ALTER TABLE SpamCustomNumber
                    ADD COLUMN setBlocked INTEGER NOT NULL DEFAULT 1
                """.trimIndent())
            }
        }
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "spam_db")
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6
                    )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("PRAGMA auto_vacuum = INCREMENTAL;")
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }

        fun runVacuumManually() {
            INSTANCE?.let { db ->
                // запустится вне транзакции
                CoroutineScope(Dispatchers.IO).launch {
                    db.openHelper.writableDatabase.execSQL("VACUUM;")
                }
            }
        }
    }
}
