package com.example.mobilerepairshop.data.model

/**
 * This is a simple data holder class, not an entity.
 * Room will use it to return the results of our custom statistics query.
 */
data class DashboardStats(
    val inCount: Int,
    val outCount: Int,
    // These are nullable because SUM can return null if there are no rows to sum
    val totalRevenue: Double?,
    val advanceReceived: Double?
)
