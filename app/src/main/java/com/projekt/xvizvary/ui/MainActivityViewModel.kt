package com.projekt.xvizvary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projekt.xvizvary.data.settings.AppLanguage
import com.projekt.xvizvary.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {
    val language: StateFlow<AppLanguage> =
        settingsRepository.language.stateIn(viewModelScope, SharingStarted.Eagerly, AppLanguage.EN)
}

