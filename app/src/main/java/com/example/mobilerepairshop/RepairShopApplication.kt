package com.example.mobilerepairshop

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mobilerepairshop.data.RepairRepository
import com.example.mobilerepairshop.data.local.RepairDatabase
import com.example.mobilerepairshop.data.workers.AutoBackupWorker
import java.util.concurrent.TimeUnit

class RepairShopApplication : Application() {

    val database by lazy { RepairDatabase.getDatabase(this) }
    val repository by lazy { RepairRepository(database.repairDao()) }

    override fun onCreate() {
        super.onCreate()
        setupAutoBackup()
    }

    private fun setupAutoBackup() {
        // Define constraints for the work
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // Only run on Wi-Fi
            .setRequiresCharging(true) // Only run when the device is charging
            .build()

        // Create a periodic work request to run once a day
        val repeatingRequest = PeriodicWorkRequestBuilder<AutoBackupWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        // Schedule the work, ensuring it's unique to prevent duplicates
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "daily_auto_backup",
            ExistingPeriodicWorkPolicy.KEEP, // Keep the existing work if it's already scheduled
            repeatingRequest
        )
    }
}
