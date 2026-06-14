package com.example.senpos.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.senpos.ui.AppViewModelProvider
import com.example.senpos.ui.components.NavBarComponent
import com.example.senpos.ui.components.TopBarComponent
import com.example.senpos.ui.navigation.Route
import com.example.senpos.viewmodels.AccountInfoViewModel

private val Orange      = Color(0xFFFF6C00)
private val OrangeLight = Color(0xFFFFF3EC)
private val CardBg      = Color(0xFFFAFAFA)

@Composable
fun AccountInfoScreen(
    navController: NavHostController,
    viewModel: AccountInfoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopBarComponent(navController)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text       = "My account",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = Orange
            )

            Card(
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    InfoRow(label = "First name", value = uiState.firstName)
                    InfoRow(label = "Last name",  value = uiState.lastName)
                    InfoRow(label = "Email",      value = uiState.email)
                }
            }

            Card(
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = OrangeLight),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text     = "Your link code",
                        fontSize = 14.sp,
                        color    = Color(0xFF000000)
                    )
                    Text(
                        text       = uiState.linkCode,
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Orange
                    )
                    Text(
                        text     = "Share this code with a family member so they can follow your medication.",
                        fontSize = 13.sp,
                        color    = Color(0xFF000000),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("Log out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        NavBarComponent(navController)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, fontSize = 13.sp, color = Color(0xFF888888))
        Text(value, fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Color(0xFF222222))
    }
}