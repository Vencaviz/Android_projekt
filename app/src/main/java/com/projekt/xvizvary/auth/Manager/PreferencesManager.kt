package com.projekt.xvizvary.auth.Manager

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    suspend fun saveUserSession(userId: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_USER_LOGGED_IN] = true
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_USER_LOGGED_IN] = false
            preferences.remove(USER_ID)
            preferences.remove(USER_EMAIL)
        }
    }

    fun isUserLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_USER_LOGGED_IN] ?: false
        }
    }

    fun getUserId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    }

    fun getUserEmail(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL]
        }
    }
}
