package com.example.mobilerepairshop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// This data class defines the structure of our database table.
// Each property corresponds to a column in the table.
@Entity(tableName = "repairs_table")
data class Repair(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val customerName: String,
    val customerContact: String,
    val alternateContact: String?, // The '?' makes this field nullable (optional)
    val imeiNumber: String?, // Nullable
    val imagePath: String?, // Nullable
    val totalCost: Double,
    val advanceTaken: Double,
    var status: String,
    val dateAdded: Long, // Storing dates as Long (timestamp) is efficient
    var dateCompleted: Long? // Nullable
)
