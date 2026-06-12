package com.example.senpos.ui.navigation

sealed class Route(val route: String) {
    object Today: Route("today")
    object AddPosology: Route("add_posology")
    object History: Route("history")
    object Login: Route("login")
    object Signup: Route("signup")
    object PharmacyMap: Route("pharmacy_map")
}