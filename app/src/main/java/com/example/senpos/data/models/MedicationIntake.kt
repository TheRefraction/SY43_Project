package com.example.senpos.data.models

data class MedicationIntake(
    val idIntake: String = "",
    val realIntakeTime: Long = 0L, // Unix timestamp
    val status: IntakeStatus = IntakeStatus.PENDING,
    val drugId: String = "",       // Reference to Drug
    val planId: String = ""        // Reference to MedicationPlan
)