package com.example.senpos.ui.screens

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
import com.example.senpos.ui.navigation.Route
import com.example.senpos.ui.theme.SenPosTheme
import com.example.senpos.viewmodels.AuthViewModel

@Composable
fun SignupScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var name     by remember { mutableStateOf("") }
    var surname  by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            navController.navigate(Route.Today.route) {
                popUpTo(Route.Signup.route) { inclusive = true }
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
        Text(
            text       = "Welcome to SenPos !",
            fontSize   = 24.sp,
            color      = Color.Black,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                    text       = "Name",
                    fontSize   = 18.sp,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value               = name,
                    onValueChange       = {
                        name = it
                        viewModel.clearError()
                    },
                    placeholder         = { Text("John") },
                    singleLine          = true,
                    modifier            = Modifier.fillMaxWidth(),
                    colors              = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor   = Color.White,
                        unfocusedBorderColor    = Color.Gray,
                        focusedBorderColor      = Color.Black,
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text       = "Surname",
                    fontSize   = 18.sp,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value               = surname,
                    onValueChange       = {
                        surname = it
                        viewModel.clearError()
                    },
                    placeholder         = { Text("Doe") },
                    singleLine          = true,
                    modifier            = Modifier.fillMaxWidth(),
                    colors              = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor   = Color.White,
                        unfocusedBorderColor    = Color.Gray,
                        focusedBorderColor      = Color.Black,
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                    onClick  = { viewModel.signup(name, surname, email, password) },
                    enabled  = !uiState.isLoading,
                    colors   = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.LightGray,
                    ),
                    border   = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text(
                        text  = if (uiState.isLoading) "Signing up..." else "Sign up",
                        color = Color.DarkGray,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
@Preview
fun SignupPreview() {
    val navController = rememberNavController()
    SenPosTheme {
        SignupScreen(navController)
    }
}