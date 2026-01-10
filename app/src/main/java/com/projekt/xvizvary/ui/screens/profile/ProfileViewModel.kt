package com.projekt.xvizvary.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.data.settings.AppLanguage
import com.projekt.xvizvary.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val language: StateFlow<AppLanguage> =
        settingsRepository.language.stateIn(viewModelScope, SharingStarted.Eagerly, AppLanguage.EN)

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }
}

