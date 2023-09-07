package com.example.storyapp.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.storyapp.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val LOGIN_KEY = booleanPreferencesKey("is_login")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_TOKEN_KEY = stringPreferencesKey("user_token")

    private val THEME_KEY = booleanPreferencesKey("theme_settings")

    suspend fun saveUserSession(user: User) {
        dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = true
            preferences[USER_ID_KEY] = user.userId
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_TOKEN_KEY] = user.token
        }
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = false
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_TOKEN_KEY)
        }
    }

    fun getUserSessionData(): Flow<User> {
        return dataStore.data.map { preferences ->
            val userId = preferences[USER_ID_KEY].toString()
            val name = preferences[USER_NAME_KEY].toString()
            val token = preferences[USER_TOKEN_KEY].toString()
            User(userId, name, token)
        }
    }

    fun getLoginStatus(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_KEY] ?: false
        }
    }

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }
}