package com.yourcompany.my_native_plugin

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@Database(
    entities = [SpamNumber::class, SpamCustomNumber::class],
    version = 4,
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

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "spam_db")
                    .addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4)
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
