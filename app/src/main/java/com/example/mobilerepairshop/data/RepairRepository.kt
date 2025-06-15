package com.example.mobilerepairshop.data

import androidx.room.Transaction
import com.example.mobilerepairshop.data.local.RepairDao
import com.example.mobilerepairshop.data.model.DashboardStats
import com.example.mobilerepairshop.data.model.Repair
import kotlinx.coroutines.flow.Flow

/**
 * The Repository class abstracts access to multiple data sources.
 * In this app, we only have one data source: the Room database.
 * It provides a clean API for data access to the rest of the application.
 */
class RepairRepository(private val repairDao: RepairDao) {

    // Expose a Flow of all repairs from the DAO. Room executes all queries on a separate thread.
    // Observed Flow will notify the UI when the data has changed.
    val allRepairs: Flow<List<Repair>> = repairDao.getAllRepairsOrderedByDate()
    val pendingCount: Flow<Int> = repairDao.getPendingCount()

    // Function to get stats for a specific date range
    fun getStatsForPeriod(startDate: Long, endDate: Long): Flow<DashboardStats?> {
        return repairDao.getStats(startDate, endDate)
    }

    // Function to search the database
    fun searchRepairs(searchQuery: String): Flow<List<Repair>> {
        return repairDao.searchDatabase(searchQuery)
    }

    // Function to get a single repair by its ID
    fun getRepairById(id: Long): Flow<Repair?> {
        return repairDao.getRepairById(id)
    }

    // The 'suspend' modifier tells the compiler that this needs to be called
    // from a coroutine or another suspending function. This ensures that
    // long-running database operations don't block the main UI thread.
    suspend fun insert(repair: Repair) {
        repairDao.insert(repair)
    }

    suspend fun update(repair: Repair) {
        repairDao.update(repair)
    }
    suspend fun delete(repair: Repair) {
        repairDao.delete(repair)
    }

    suspend fun getAllRepairsForBackup(): List<Repair> {
        return repairDao.getAllRepairsForBackup()
    }

    @Transaction
    suspend fun restoreFromBackup(repairs: List<Repair>) {
        repairDao.clearAll()
        repairs.forEach { repairDao.insert(it) }
    }
}
