package com.example.lexicon

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lexicon.data.AppDatabase
import com.example.lexicon.data.SettingsRepository
import com.example.lexicon.data.VocabularyRepository

class LexiconApp : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var repository: VocabularyRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE vocabulary ADD COLUMN lastViewedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE vocabulary ADD COLUMN consecutiveCorrectAfterStruggling INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Empty migration
            }
        }

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "lexicon_database"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()

        repository = VocabularyRepository(database.wordDao())
        settingsRepository = SettingsRepository(this)
    }
}
