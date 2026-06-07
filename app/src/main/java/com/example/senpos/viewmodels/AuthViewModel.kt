package com.example.senpos.viewmodels

import androidx.lifecycle.ViewModel
import com.example.senpos.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class AuthUiState(
    val currentUser: User? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

private val dummyUsers = listOf(
    User(
        id    = "user_1",
        firstName = "Waltuh",
        lastName  = "HelpMe",
        email     = "im.a@labubu.waltuh",
        password  = "feet"
    )
)


class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        val user = dummyUsers.find { it.email == email && it.password == password }

        _uiState.value = if (user != null) {
            _uiState.value.copy(
                currentUser = user,
                isLoggedIn  = true,
                isLoading   = false
            )
        } else {
            _uiState.value.copy(
                isLoading    = false,
                errorMessage = "Email ou mot de passe incorrect."
            )
        }
    }

    fun logout() {
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}