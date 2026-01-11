package com.projekt.xvizvary.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.auth.Manager.SessionManager
import com.projekt.xvizvary.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

sealed class SignUpEvent {
    data object SignUpSuccess : SignUpEvent()
    data class SignUpError(val message: String) : SignUpEvent()
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SignUpEvent>()
    val events: SharedFlow<SignUpEvent> = _events.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, confirmPasswordError = null)
    }

    fun signUp() {
        val state = _uiState.value

        // Validation
        var hasError = false

        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.value = _uiState.value.copy(emailError = "error_invalid_email")
            hasError = true
        }

        if (state.password.isBlank() || state.password.length < 6) {
            _uiState.value = _uiState.value.copy(passwordError = "error_invalid_password")
            hasError = true
        }

        if (state.password != state.confirmPassword) {
            _uiState.value = _uiState.value.copy(confirmPasswordError = "error_passwords_dont_match")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = registerUseCase(state.email.trim(), state.password)

            result.fold(
                onSuccess = {
                    // Sync data from cloud after successful registration
                    _uiState.value = _uiState.value.copy(isLoading = false, isSyncing = true)
                    sessionManager.syncAfterLogin()
                    _uiState.value = _uiState.value.copy(isSyncing = false)
                    _events.emit(SignUpEvent.SignUpSuccess)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _events.emit(SignUpEvent.SignUpError(
                        exception.message ?: "Registration failed"
                    ))
                }
            )
        }
    }
}
