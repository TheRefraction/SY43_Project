package fr.utbm.sy43.pilulito.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.utbm.sy43.pilulito.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val email: String = "",
    val newEmail: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

class SettingsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.value = _uiState.value.copy(email = user.email)
                }
            }
        }
    }

    fun onNewEmailChanged(value: String) {
        _uiState.value = _uiState.value.copy(newEmail = value, errorMessage = null, successMessage = null)
    }

    fun onNewPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, errorMessage = null, successMessage = null)
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = null, successMessage = null)
    }

    fun saveEmail() {
        val state = _uiState.value
        if (state.newEmail.isBlank() || !state.newEmail.contains("@")) {
            _uiState.value = state.copy(errorMessage = "Please enter a valid email.")
            return
        }
        viewModelScope.launch {
            authRepository.updateEmail(state.newEmail)
            _uiState.value = _uiState.value.copy(
                email        = state.newEmail,
                newEmail     = "",
                successMessage = "Email updated."
            )
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            authRepository.deleteAccount()
        }
    }

    fun savePassword() {
        val state = _uiState.value
        if (state.newPassword.length < 4) {
            _uiState.value = state.copy(errorMessage = "Password must be at least 4 characters.")
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.value = state.copy(errorMessage = "Passwords do not match.")
            return
        }
        viewModelScope.launch {
            authRepository.updatePassword(state.newPassword)
            _uiState.value = _uiState.value.copy(
                newPassword     = "",
                confirmPassword = "",
                successMessage  = "Password updated."
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}