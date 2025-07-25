package com.example.mobilerepairshop.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mobilerepairshop.data.model.DashboardStats
import com.example.mobilerepairshop.data.model.Repair
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairDao {

    @Insert
    suspend fun insert(repair: Repair)

    @Update
    suspend fun update(repair: Repair)

    @Delete
    suspend fun delete(repair: Repair)

    @Query("SELECT * FROM repairs_table ORDER BY dateAdded DESC")
    suspend fun getAllRepairsForBackup(): List<Repair>

    @Query("DELETE FROM repairs_table")
    suspend fun clearAll()

    @Query("SELECT * FROM repairs_table WHERE id = :id")
    fun getRepairById(id: Long): Flow<Repair?>

    @Query("SELECT * FROM repairs_table ORDER BY dateAdded DESC")
    fun getAllRepairsOrderedByDate(): Flow<List<Repair>>

    @Query("SELECT * FROM repairs_table WHERE customerName LIKE '%' || :searchQuery || '%' OR customerContact LIKE '%' || :searchQuery || '%' ORDER BY dateAdded DESC")
    fun searchDatabase(searchQuery: String): Flow<List<Repair>>

    // --- THIS IS THE NEW, MORE POWERFUL QUERY ---
    @Query("""
        SELECT
            (SELECT COUNT(*) FROM repairs_table WHERE dateAdded BETWEEN :startDate AND :endDate) as inCount,
            (SELECT COUNT(*) FROM repairs_table WHERE status = 'Out' AND dateCompleted BETWEEN :startDate AND :endDate) as outCount,
            (SELECT SUM(totalCost) FROM repairs_table WHERE dateAdded BETWEEN :startDate AND :endDate) as estimatedRevenue,
            (SELECT SUM(advanceTaken) FROM repairs_table WHERE status != 'Out' AND dateAdded BETWEEN :startDate AND :endDate) as advanceFromPending,
            (SELECT SUM(totalCost) FROM repairs_table WHERE status = 'Out' AND dateCompleted BETWEEN :startDate AND :endDate) as revenueFromOut,
            (SELECT SUM(totalCost - advanceTaken) FROM repairs_table WHERE status != 'Out' AND dateAdded BETWEEN :startDate AND :endDate) as upcomingRevenue
    """)
    fun getStats(startDate: Long, endDate: Long): Flow<DashboardStats?>
    // --- END OF NEW QUERY ---

    @Query("SELECT COUNT(*) FROM repairs_table WHERE status = 'Pending'")
    fun getPendingCount(): Flow<Int>

}
