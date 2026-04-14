package com.example.senpos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.senpos.ui.navigation.Route

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Today.route
    ) {
        composable(Route.Today.route) {
            // TODO la vue d'aujourd'hui
        }

        composable(Route.AddMedicationDosage.route) {
            // TODO la vue d'ajout de medocs
        }

        composable(Route.History.route) {
            // TODO la vue de calendrier
        }
    }
}