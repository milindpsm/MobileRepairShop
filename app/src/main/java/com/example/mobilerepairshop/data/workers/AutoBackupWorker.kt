package com.example.mobilerepairshop.data.workers

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mobilerepairshop.RepairShopApplication
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AutoBackupWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Get the repository from the application class
        val repository = (applicationContext as RepairShopApplication).repository

        return try {
            val repairs = repository.getAllRepairsForBackup()
            val gson = Gson()
            val jsonString = gson.toJson(repairs)

            // Create a specific directory for auto backups
            val backupDir = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "auto_backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "auto_backup_${sdf.format(Date())}.json"
            val backupFile = File(backupDir, fileName)

            FileOutputStream(backupFile).use {
                it.write(jsonString.toByteArray())
            }

            // Prune old backups, keeping only the last 7
            pruneOldBackups(backupDir)

            // Indicate that the work finished successfully
            Result.success()
        } catch (e: Exception) {
            // Indicate that the work failed
            Result.failure()
        }
    }

    private fun pruneOldBackups(backupDir: File) {
        val files = backupDir.listFiles { file -> file.isFile && file.name.startsWith("auto_backup_") }
        if (files != null && files.size > 7) {
            // Sort files by name (which includes the date) ascending, so the oldest are first
            files.sortBy { it.name }
            // Delete all but the most recent 7
            val filesToDelete = files.size - 7
            for (i in 0 until filesToDelete) {
                files[i].delete()
            }
        }
    }
}
