package com.example.senpos.data.models

data class MedicationPlan(
    val idPlan: String = "",
    val startDate: Long = 0L,      // Unix timestamp
    val endDate: Long = 0L,        // Unix timestamp
    val frequency: String = "",    // e.g. "Every 8 hours"
    val quantity: Int = 0,
    val userId: String = ""        // Reference to User
)