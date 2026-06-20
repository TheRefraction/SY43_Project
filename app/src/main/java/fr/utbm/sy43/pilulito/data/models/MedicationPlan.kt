package fr.utbm.sy43.pilulito.data.models

data class MedicationPlan(
    val id: String = "",
    val startDate: Long = 0L,      // Unix timestamp
    val endDate: Long = 0L,        // Unix timestamp
    val frequency: Long = 0L,    // e.g. "Every 8 hours"
    val quantity: Int = 1,
    val userId: String = ""       // Reference to User
)