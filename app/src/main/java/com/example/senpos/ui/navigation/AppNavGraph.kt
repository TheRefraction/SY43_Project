package com.example.senpos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.senpos.ui.screens.AccountInfoScreen
import com.example.senpos.ui.screens.AddPosologyScreen
import com.example.senpos.ui.screens.HistoryScreen
import com.example.senpos.ui.screens.LoginScreen
import com.example.senpos.ui.screens.PharmacyMapScreen
import com.example.senpos.ui.screens.SignupScreen
import com.example.senpos.ui.screens.TodayScreen
import com.example.senpos.ui.screens.SupervisorScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Login.route
    ) {
        composable(Route.Today.route) {
            TodayScreen(navController)
        }

        composable(Route.AddPosology.route) {
            AddPosologyScreen(navController)
        }

        composable(Route.History.route) {
            HistoryScreen(navController)
        }

        composable(Route.Login.route) {
            LoginScreen(navController)
        }

        composable(Route.Signup.route) {
            SignupScreen(navController)
        }

        composable(Route.PharmacyMap.route) {
            PharmacyMapScreen(navController)
        }

        composable(Route.Supervisor.route) {
            SupervisorScreen(navController)
        }

        composable(Route.Profile.route) {
            AccountInfoScreen(navController)
        }
    }
}