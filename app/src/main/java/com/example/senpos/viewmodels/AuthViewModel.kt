package com.example.senpos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.senpos.data.models.AccountType
import com.example.senpos.data.models.User
import com.example.senpos.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: User? = null,
    val isLoggedIn: Boolean = false,
    val isSignupSuccessful: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    isLoggedIn = user != null
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val user = authRepository.login(email, password)
            if (user == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Email ou mot de passe incorrect."
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun signup(
        name: String,
        surname: String,
        email: String,
        password: String,
        accountType: AccountType,
        supervisorLinkCode: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isSignupSuccessful = false)

            if (accountType == AccountType.SUPERVISOR && supervisorLinkCode.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Please enter the senior's link code."
                )
                return@launch
            }

            val user = authRepository.signup(name, surname, email, password, accountType, supervisorLinkCode)
            if (user == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = if (accountType == AccountType.SUPERVISOR)
                        "Invalid link code or email already used."
                    else
                        "Error during signup."
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, isSignupSuccessful = true)
            }
        }
    }

    fun resetSignupState() {
        _uiState.value = _uiState.value.copy(isSignupSuccessful = false)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}