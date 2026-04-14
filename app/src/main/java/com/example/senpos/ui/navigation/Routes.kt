package com.example.senpos.ui.navigation

sealed class Route(val route: String) {
    object Today: Route("today")
    object AddMedicationDosage: Route("add_med_dosage")
    object History: Route("history")
}