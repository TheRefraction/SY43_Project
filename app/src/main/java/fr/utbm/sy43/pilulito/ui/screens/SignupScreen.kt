package fr.utbm.sy43.pilulito.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import fr.utbm.sy43.pilulito.data.models.AccountType
import fr.utbm.sy43.pilulito.ui.AppViewModelProvider
import fr.utbm.sy43.pilulito.ui.theme.SenPosTheme
import fr.utbm.sy43.pilulito.viewmodels.AuthViewModel

private val GreenBg = Color(0xFF228B22)
private val Orange  = Color(0xFFFF6C00)

@Composable
fun SignupScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    var name               by remember { mutableStateOf("") }
    var surname            by remember { mutableStateOf("") }
    var email              by remember { mutableStateOf("") }
    var password           by remember { mutableStateOf("") }
    var accountType        by remember { mutableStateOf(AccountType.SENIOR) }
    var supervisorLinkCode by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isSignupSuccessful) {
        if (uiState.isSignupSuccessful) {
            viewModel.resetSignupState()
            navController.popBackStack()
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
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

        AccountTypeSelector(
            selected = accountType,
            onSelect = {
                accountType = it
                viewModel.clearError()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .background(GreenBg, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.padding(horizontal = 32.dp)
            ) {
                LabeledField("Name", name, { name = it; viewModel.clearError() }, "John")
                Spacer(modifier = Modifier.height(12.dp))

                LabeledField("Surname", surname, { surname = it; viewModel.clearError() }, "Doe")
                Spacer(modifier = Modifier.height(12.dp))

                LabeledField("Login", email, { email = it; viewModel.clearError() }, "johndoe@email.com")
                Spacer(modifier = Modifier.height(12.dp))

                LabeledField(
                    label       = "Password",
                    value       = password,
                    onValueChange = { password = it; viewModel.clearError() },
                    placeholder = "Password",
                    isPassword  = true
                )

                if (accountType == AccountType.SUPERVISOR) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LabeledField(
                        label         = "Senior's link code",
                        value         = supervisorLinkCode,
                        onValueChange = {
                            supervisorLinkCode = it.uppercase()
                            viewModel.clearError()
                        },
                        placeholder   = "e.g. WALT-1234"
                    )
                    Text(
                        text     = "Ask the senior for their code, visible on their profile.",
                        color    = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

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
                    onClick = {
                        viewModel.signup(
                            name, surname, email, password,
                            accountType,
                            supervisorLinkCode.takeIf { accountType == AccountType.SUPERVISOR }
                        )
                    },
                    enabled = !uiState.isLoading,
                    colors  = ButtonDefaults.outlinedButtonColors(containerColor = Color.LightGray),
                    border  = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text(
                        text  = if (uiState.isLoading) "Signing up..." else "Sign up",
                        color = Color.DarkGray,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text(
                text     = "Already have an account? Login here",
                fontSize = 18.sp,
                color    = Orange,
            )
        }
    }
}


@Composable
private fun AccountTypeSelector(
    selected: AccountType,
    onSelect: (AccountType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AccountTypeButton(
            label    = "I'm a senior",
            selected = selected == AccountType.SENIOR,
            onClick  = { onSelect(AccountType.SENIOR) },
            modifier = Modifier.weight(1f)
        )
        AccountTypeButton(
            label    = "I'm a supervisor",
            selected = selected == AccountType.SUPERVISOR,
            onClick  = { onSelect(AccountType.SUPERVISOR) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AccountTypeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.height(56.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) Orange else Color.White,
            contentColor   = if (selected) Color.White else Orange
        ),
        border = BorderStroke(1.dp, Orange)
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Text(text = label, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = { Text(placeholder) },
        singleLine    = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier      = Modifier.fillMaxWidth(),
        colors        = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor   = Color.White,
            unfocusedBorderColor    = Color.Gray,
            focusedBorderColor      = Color.Black,
        )
    )
}

@Composable
@Preview
fun SignupPreview() {
    val navController = rememberNavController()
    SenPosTheme {
        SignupScreen(navController)
    }
}