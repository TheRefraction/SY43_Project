package fr.utbm.sy43.pilulito.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import fr.utbm.sy43.pilulito.ui.AppViewModelProvider
import fr.utbm.sy43.pilulito.ui.components.IntakeComponent
import fr.utbm.sy43.pilulito.ui.components.NavBarComponent
import fr.utbm.sy43.pilulito.ui.components.OverdueIntakeComponent
import fr.utbm.sy43.pilulito.ui.components.TopBarComponent
import fr.utbm.sy43.pilulito.ui.theme.SenPosTheme
import fr.utbm.sy43.pilulito.viewmodels.HomeViewModel
import kotlinx.coroutines.delay

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

                itemsIndexed(items = uiState.todayIntakes) { index, intake ->
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(index * 100L)
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(durationMillis = 400)
                        ) + fadeIn(animationSpec = tween(durationMillis = 400))
                    ) {
                        IntakeComponent(
                            intake = intake,
                            onTake = { viewModel.markAsTaken(it) }
                        )
                    }
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