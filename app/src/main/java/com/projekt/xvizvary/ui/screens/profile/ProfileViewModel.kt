package com.projekt.xvizvary.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.auth.Manager.SessionManager
import com.projekt.xvizvary.data.settings.AppLanguage
import com.projekt.xvizvary.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userEmail: String? = null,
    val isLoading: Boolean = false
)

sealed class ProfileEvent {
    data object LoggedOut : ProfileEvent()
    data class Error(val message: String) : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val language: StateFlow<AppLanguage> =
        settingsRepository.language.stateIn(viewModelScope, SharingStarted.Eagerly, AppLanguage.EN)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val currentUser = sessionManager.getCurrentUser()
        _uiState.value = ProfileUiState(
            userEmail = currentUser?.email
        )
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = sessionManager.logout()

            result.fold(
                onSuccess = {
                    _events.emit(ProfileEvent.LoggedOut)
                },
                onFailure = { exception ->
                    _events.emit(ProfileEvent.Error(exception.message ?: "Logout failed"))
                }
            )

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}

