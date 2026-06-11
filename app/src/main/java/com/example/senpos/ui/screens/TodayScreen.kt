package com.example.senpos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.senpos.ui.AppViewModelProvider
import com.example.senpos.ui.components.IntakeComponent
import com.example.senpos.ui.components.NavBarComponent
import com.example.senpos.ui.components.OverdueIntakeComponent
import com.example.senpos.ui.components.TopBarComponent
import com.example.senpos.ui.theme.SenPosTheme
import com.example.senpos.viewmodels.HomeViewModel

@Composable
fun TodayScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showOverdueDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissOverdueDialog() },
            shape            = RoundedCornerShape(24.dp),
            containerColor   = Color(0xFFFFF3EC),
            title = {
                Text(
                    text     = "Missed medications",
                    fontSize = 20.sp,
                    color    = Color(0xFFFF6C00)
                )
            },
            text = {
                LazyColumn {
                    items(
                        items = uiState.overdueIntakes,
                        key   = { it.intakeId }
                    ) { intake ->
                        OverdueIntakeComponent(
                            intake = intake,
                            onTake = { viewModel.markOverdueAsTaken(it) },
                            onMiss = { viewModel.markOverdueAsMissed(it) }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissOverdueDialog() }) {
                    Text("Close", color = Color(0xFFFF6C00))
                }
            }
        )
    }

    Column {
        TopBarComponent(navController)

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(32.dp),
                color = Color(0xFFFF6C00)
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items = uiState.todayIntakes,
                    key   = { it.intakeId }
                ) { intake ->
                    IntakeComponent(
                        intake = intake,
                        onTake = { viewModel.markAsTaken(it) }
                    )
                }

                if (uiState.todayIntakes.isEmpty()) {
                    item {
                        Text(
                            text     = "No medications scheduled for today.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            color    = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

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