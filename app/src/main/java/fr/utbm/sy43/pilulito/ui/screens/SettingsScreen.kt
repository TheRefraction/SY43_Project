package fr.utbm.sy43.pilulito.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fr.utbm.sy43.pilulito.ui.AppViewModelProvider
import fr.utbm.sy43.pilulito.ui.components.NavBarComponent
import fr.utbm.sy43.pilulito.ui.components.TopBarComponent
import fr.utbm.sy43.pilulito.ui.navigation.Route
import fr.utbm.sy43.pilulito.ui.theme.LocalThemeState
import fr.utbm.sy43.pilulito.viewmodels.SettingsViewModel


private val Orange      = Color(0xFFFF6C00)
private val OrangeLight = Color(0xFFFFF3EC)
private val CardBg      = Color(0xFFFAFAFA)

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState    by viewModel.uiState.collectAsState()
    val themeState  = LocalThemeState.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopBarComponent(navController)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text       = "Settings",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = Orange
            )


            SettingsCard(title = "Appearance") {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark mode", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(
                            if (themeState.isDark) "Dark theme enabled"
                            else "Light theme enabled",
                            fontSize = 13.sp,
                            color    = Color.Gray
                        )
                    }
                    Switch(
                        checked         = themeState.isDark,
                        onCheckedChange = { themeState.isDark = it },
                        colors          = SwitchDefaults.colors(
                            checkedThumbColor  = Color.White,
                            checkedTrackColor  = Orange,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
            }

            SettingsCard(title = "Change email") {
                Text(
                    text     = "Current: ${uiState.email}",
                    fontSize = 13.sp,
                    color    = Color.Gray
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value         = uiState.newEmail,
                    onValueChange = { viewModel.onNewEmailChanged(it) },
                    label         = { Text("New email") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = settingsFieldColors()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick  = { viewModel.saveEmail() },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    Text("Update email")
                }
            }

            SettingsCard(title = "Change password") {
                OutlinedTextField(
                    value         = uiState.newPassword,
                    onValueChange = { viewModel.onNewPasswordChanged(it) },
                    label         = { Text("New password") },
                    singleLine    = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = settingsFieldColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value         = uiState.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                    label         = { Text("Confirm password") },
                    singleLine    = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = settingsFieldColors()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick  = { viewModel.savePassword() },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    Text("Update password")
                }
            }

            uiState.successMessage?.let {
                Text(it, color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }

            Spacer(Modifier.height(8.dp))
        }

        var showDeleteDialog by remember { mutableStateOf(false) }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                shape            = RoundedCornerShape(20.dp),
                title            = { Text("Delete account", fontWeight = FontWeight.Bold) },
                text             = { Text("This action is permanent. Your account will be deleted.") },
                confirmButton    = {
                    TextButton(onClick = {
                        viewModel.deleteAccount(onAccountDeleted = {
                            navController.navigate(Route.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }})
                    }) {
                        Text("Delete", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton    = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Button(
            onClick  = { showDeleteDialog = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape    = RoundedCornerShape(16.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935).copy(alpha = 0.1f),
                contentColor   = Color(0xFFE53935)
            )
        ) {
            Text("Delete account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        NavBarComponent(navController)
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
private fun settingsFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Orange,
    unfocusedBorderColor = Color(0xFFDDDDDD),
    focusedLabelColor    = Orange
)