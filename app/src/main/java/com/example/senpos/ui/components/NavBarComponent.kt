package com.example.senpos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.senpos.R
import com.example.senpos.ui.navigation.Route
import com.example.senpos.ui.theme.SenPosTheme

@Composable
fun NavBarComponent(navController: NavHostController) {
    Row(modifier = Modifier.fillMaxWidth()
        .background(color = Color(0xFF228B22))
        .padding(vertical = 24.dp),

        horizontalArrangement = Arrangement.SpaceAround) {


        Button(onClick = { navController.navigate(Route.AddMedicationDosage.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_a_medication_and_dosage),
                modifier = Modifier.size(48.dp)
            )
        }

        Button(onClick = { navController.navigate(Route.PharmacyMap.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
            modifier = Modifier.widthIn(max = 180.dp)
        ) {
            Text(
                text = "Pharmacies near me",
                fontSize = 24.sp
            )
        }

        Button(onClick = { navController.navigate(Route.History.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = stringResource(R.string.review_previous_days),
                modifier = Modifier.size(48.dp)
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