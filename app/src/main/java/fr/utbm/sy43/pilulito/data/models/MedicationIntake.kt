package fr.utbm.sy43.pilulito.data.models

enum class IntakeStatus {
    PENDING,
    TAKEN,
    MISSED,
}

data class MedicationIntake(
    val id: String ="",
    val realIntakeTime: Long = 0L, // Unix timestamp
    var status: IntakeStatus = IntakeStatus.PENDING,
    val drugId: String = "",       // Reference to Drug
    val planId: String = ""       // Reference to MedicationPlan
)