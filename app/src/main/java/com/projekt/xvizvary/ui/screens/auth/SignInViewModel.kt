package com.projekt.xvizvary.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.auth.LoginUseCase
import com.projekt.xvizvary.auth.Manager.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
)

sealed class SignInEvent {
    data object SignInSuccess : SignInEvent()
    data class SignInError(val message: String) : SignInEvent()
}

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SignInEvent>()
    val events: SharedFlow<SignInEvent> = _events.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null)
    }

    fun signIn() {
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

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = loginUseCase(state.email.trim(), state.password)

            result.fold(
                onSuccess = {
                    // Sync data from cloud after successful login
                    _uiState.value = _uiState.value.copy(isLoading = false, isSyncing = true)
                    sessionManager.syncAfterLogin()
                    _uiState.value = _uiState.value.copy(isSyncing = false)
                    _events.emit(SignInEvent.SignInSuccess)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _events.emit(SignInEvent.SignInError(
                        exception.message ?: "Authentication failed"
                    ))
                }
            )
        }
    }
}
