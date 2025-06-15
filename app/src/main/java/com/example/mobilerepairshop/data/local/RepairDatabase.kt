package com.example.mobilerepairshop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mobilerepairshop.data.model.Repair

// IMPORTANT: Increased version number from 1 to 2
@Database(entities = [Repair::class], version = 2, exportSchema = false)
abstract class RepairDatabase : RoomDatabase() {

    abstract fun repairDao(): RepairDao

    companion object {
        @Volatile
        private var INSTANCE: RepairDatabase? = null

        // NEW: Define the migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // SQL command to add the new 'description' column to our existing table
                db.execSQL("ALTER TABLE repairs_table ADD COLUMN description TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): RepairDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RepairDatabase::class.java,
                    "repair_database"
                )
                    // Add the migration path to the builder
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
