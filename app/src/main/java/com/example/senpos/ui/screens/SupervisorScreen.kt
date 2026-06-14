package com.example.senpos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.example.senpos.data.models.IntakeStatus
import com.example.senpos.ui.AppViewModelProvider
import com.example.senpos.ui.navigation.Route
import com.example.senpos.viewmodels.SupervisorLogEntry
import com.example.senpos.viewmodels.SupervisorViewModel
import java.text.SimpleDateFormat
import java.util.*

private val Green = Color(0xFF4CAF50)
private val Red   = Color(0xFFE53935)
private val Grey  = Color(0xFFBDBDBD)
private val CardBg = Color(0xFFFAFAFA)

@Composable
fun SupervisorScreen(
    navController: NavHostController,
    viewModel: SupervisorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6C00))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Medication log",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )

                IconButton(onClick = {
                    viewModel.logout()
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Log out",
                        tint = Color.White
                    )
                }
            }
        }

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF6C00))
                }
            }
            uiState.errorMessage != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.errorMessage!!, color = Color.Gray, fontSize = 16.sp)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.entries, key = { it.intakeId }) { entry ->
                        LogRow(entry)
                    }

                    if (uiState.entries.isEmpty()) {
                        item {
                            Text(
                                text     = "No medication history yet.",
                                color    = Color.Gray,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogRow(entry: SupervisorLogEntry) {
    val (statusColor, statusLabel) = when (entry.status) {
        IntakeStatus.TAKEN   -> Green to "Taken"
        IntakeStatus.MISSED  -> Red to "Missed"
        IntakeStatus.PENDING -> Grey to "Pending"
    }

    val dateFormat = remember { SimpleDateFormat("EEE dd/MM — HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(entry.drugName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(entry.dosage, fontSize = 13.sp, color = Color.Gray)
            Text(dateFormat.format(Date(entry.scheduledTime)), fontSize = 12.sp, color = Color.Gray)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(statusLabel, color = statusColor, fontWeight = FontWeight.Medium, fontSize = 13.sp)
        }
    }
}