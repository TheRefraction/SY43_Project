package fr.utbm.sy43.pilulito.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import fr.utbm.sy43.pilulito.data.models.AccountType
import fr.utbm.sy43.pilulito.ui.AppViewModelProvider
import fr.utbm.sy43.pilulito.ui.navigation.Route
import fr.utbm.sy43.pilulito.ui.theme.SenPosTheme
import fr.utbm.sy43.pilulito.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            val destination = when (uiState.currentUser?.accountType) {
                AccountType.SUPERVISOR -> Route.Supervisor.route
                else                   -> Route.Today.route
            }
            navController.navigate(destination) {
                popUpTo(Route.Login.route) { inclusive = true }
            }
        }
    }

    Column(
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally,
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFF228B22), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.padding(horizontal = 32.dp)
            ) {
                Text(
                    text       = "Login",
                    fontSize   = 18.sp,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value         = email,
                    onValueChange = {
                        email = it
                        viewModel.clearError()
                    },
                    placeholder   = { Text("johndoe@email.com") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor   = Color.White,
                        unfocusedBorderColor    = Color.Gray,
                        focusedBorderColor      = Color.Black,
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text       = "Password",
                    fontSize   = 18.sp,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value               = password,
                    onValueChange       = {
                        password = it
                        viewModel.clearError()
                    },
                    placeholder         = { Text("Password") },
                    singleLine          = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier            = Modifier.fillMaxWidth(),
                    colors              = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor   = Color.White,
                        unfocusedBorderColor    = Color.Gray,
                        focusedBorderColor      = Color.Black,
                    )
                )

                // Message d'erreur
                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text     = uiState.errorMessage!!,
                        color    = Color(0xFFFFCCCC),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick  = { viewModel.login(email, password) },
                    enabled  = !uiState.isLoading,
                    colors   = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.LightGray,
                    ),
                    border   = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text(
                        text  = if (uiState.isLoading) "Signing in..." else "Sign in",
                        color = Color.DarkGray,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        TextButton(onClick = { navController.navigate(Route.Signup.route) }) {
            Text(
                text     = "Don't have an account? Click here",
                fontSize = 18.sp,
                color    = Color(0xFFFF6C00),
            )
        }
    }
}

@Composable
@Preview
fun LoginPreview() {
    val navController = rememberNavController()
    SenPosTheme {
        LoginScreen(navController)
    }
}