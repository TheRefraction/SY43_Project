package com.example.senpos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.senpos.notifications.NotificationHelper
import com.example.senpos.ui.navigation.Route
import com.example.senpos.ui.theme.SenPosTheme

@Composable
fun TopBarComponent(navController: NavHostController) {
    Row(modifier = Modifier.fillMaxWidth()
        .background(color = Color(0xFF228B22))
        .padding(top = 28.dp, bottom = 16.dp),

        horizontalArrangement = Arrangement.SpaceAround) {

        val context = LocalContext.current

        Button(
            onClick = {
                navController.navigate(Route.Profile.route)
            },

            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "Manage Account",
                modifier = Modifier.size(48.dp)
            )
        }

        Button(onClick = { navController.navigate(Route.Today.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
            modifier = Modifier.widthIn(max = 180.dp)
        ) {
            Text(
                text = "Today",
                fontSize = 42.sp
            )
        }

        Button(onClick = { navController.navigate(Route.History.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "App settings",
                modifier = Modifier.size(48.dp)
            )
        }

    }
}

@Preview
@Composable
fun TopBarPreview() {
    val navController = rememberNavController()
    SenPosTheme {
        TopBarComponent(navController)
    }
}