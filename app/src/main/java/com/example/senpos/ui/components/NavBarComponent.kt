package com.example.senpos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.senpos.R
import com.example.senpos.ui.navigation.Route
import com.example.senpos.ui.theme.SenPosTheme

@Composable
fun NavBarComponent(navController: NavHostController) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround) {

        Button(onClick = { navController.navigate(Route.Today.route) }) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = stringResource(R.string.check_daily_medication_intake)
            )
        }

        Button(onClick = { navController.navigate(Route.AddMedicationDosage.route) }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_a_medication_and_dosage)
            )
        }

        Button(onClick = { navController.navigate(Route.History.route) }) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = stringResource(R.string.review_previous_days)
            )
        }

    }
}

@Preview
@Composable
fun NavBarPreview() {
    val navController = rememberNavController()
    SenPosTheme {
        NavBarComponent(navController)
    }
}