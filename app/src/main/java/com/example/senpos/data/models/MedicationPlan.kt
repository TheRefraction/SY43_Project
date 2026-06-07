package com.example.senpos.data.models

data class MedicationPlan(
    val id: String,
    val startDate: Long,      // Unix timestamp
    val endDate: Long,        // Unix timestamp
    val frequency: Long,    // e.g. "Every 8 hours"
    val quantity: Int,
    val userId: String       // Reference to User
)