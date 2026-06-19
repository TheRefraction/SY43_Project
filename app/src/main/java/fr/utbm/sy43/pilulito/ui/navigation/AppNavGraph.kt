package fr.utbm.sy43.pilulito.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.utbm.sy43.pilulito.ui.screens.AccountInfoScreen
import fr.utbm.sy43.pilulito.ui.screens.AddPosologyScreen
import fr.utbm.sy43.pilulito.ui.screens.HistoryScreen
import fr.utbm.sy43.pilulito.ui.screens.LoginScreen
import fr.utbm.sy43.pilulito.ui.screens.PharmacyMapScreen
import fr.utbm.sy43.pilulito.ui.screens.SettingsScreen
import fr.utbm.sy43.pilulito.ui.screens.SignupScreen
import fr.utbm.sy43.pilulito.ui.screens.TodayScreen
import fr.utbm.sy43.pilulito.ui.screens.SupervisorScreen

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

        composable(Route.Settings.route) {
            SettingsScreen(navController)
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