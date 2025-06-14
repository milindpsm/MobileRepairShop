package com.example.mobilerepairshop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mobilerepairshop.data.model.Repair

// Annotates class to be a Room Database with a table (entity) of the Repair class.
// version = 1 is the initial version of the database schema.
// exportSchema = false is to avoid a build warning about schema exportation.
@Database(entities = [Repair::class], version = 1, exportSchema = false)
abstract class RepairDatabase : RoomDatabase() {

    // The database exposes DAOs through an abstract "getter" method for each @Dao.
    abstract fun repairDao(): RepairDao

    // Companion object allows clients to access the methods for creating or
    // getting the database without instantiating the class.
    companion object {
        // @Volatile annotation means that writes to this field are immediately
        // made visible to other threads.
        @Volatile
        private var INSTANCE: RepairDatabase? = null

        fun getDatabase(context: Context): RepairDatabase {
            // If the INSTANCE is not null, then return it,
            // if it is, then create the database.
            // This is called the Singleton pattern. It's used here to prevent
            // multiple instances of the database opening at the same time.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RepairDatabase::class.java,
                    "repair_database" // This is the actual file name of the database on the device
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
