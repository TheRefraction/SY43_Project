package com.example.senpos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.senpos.ui.components.NavBarComponent
import com.example.senpos.ui.theme.SenPosTheme

@Composable
fun TodayScreen(navController: NavHostController) {
    Column {
        Text(
            text = "To take today",
            fontSize = 32.sp
            )
        Spacer(modifier = Modifier.weight(1f))
        NavBarComponent(navController)
    }
}

@Preview
@Composable
fun TodayPreview() {
    val navController = rememberNavController()
    SenPosTheme {
        TodayScreen(navController)
    }
}