package com.projekt.xvizvary.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val language: Flow<AppLanguage> =
        dataStore.data.map { prefs -> AppLanguage.fromLanguageTag(prefs[KEY_LANGUAGE_TAG]) }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE_TAG] = language.languageTag
        }
    }

    private companion object {
        val KEY_LANGUAGE_TAG = stringPreferencesKey("language_tag")
    }
}

