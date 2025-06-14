package com.example.mobilerepairshop

import android.app.Application
import com.example.mobilerepairshop.data.RepairRepository
import com.example.mobilerepairshop.data.local.RepairDatabase

/**
 * Custom Application class to hold a single instance of the database and repository.
 */
class RepairShopApplication : Application() {
    // Using by lazy so the database and repository are only created when they're needed
    // rather than when the application starts.
    val database by lazy { RepairDatabase.getDatabase(this) }
    val repository by lazy { RepairRepository(database.repairDao()) }
}
