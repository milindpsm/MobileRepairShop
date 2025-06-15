package com.example.mobilerepairshop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repairs_table")
data class Repair(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    var customerName: String,
    var customerContact: String,
    var alternateContact: String?,
    var imeiNumber: String?,
    // NEW FIELD
    var description: String,
    var imagePath: String?,
    var totalCost: Double,
    var advanceTaken: Double,
    var status: String,
    val dateAdded: Long,
    var dateCompleted: Long?
)
