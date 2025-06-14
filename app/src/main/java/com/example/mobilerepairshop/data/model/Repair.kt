package com.example.mobilerepairshop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// This data class defines the structure of our database table.
// Each property corresponds to a column in the table.
@Entity(tableName = "repairs_table")
data class Repair(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Should always be val

    var customerName: String,
    var customerContact: String,
    var alternateContact: String?,
    var imeiNumber: String?,
    var imagePath: String?,
    var totalCost: Double, // Changed from val to var
    var advanceTaken: Double, // Changed from val to var
    var status: String,
    val dateAdded: Long, // Should always be val
    var dateCompleted: Long?
)
