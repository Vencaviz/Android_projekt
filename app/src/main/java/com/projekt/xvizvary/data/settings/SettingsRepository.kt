package com.projekt.xvizvary.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val language: Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)
}

