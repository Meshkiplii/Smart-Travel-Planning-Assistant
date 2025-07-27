package com.meshkipli.smarttravel.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.meshkipli.smarttravel.data.remote.UserDto // Your UserDto from network responses
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

// Extension property to create the DataStore instance (top-level in the file)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserSession(
    val token: String?,
    val user: UserDto?,
    val isLoggedIn: Boolean
)

class UserPreferencesRepository(private val context: Context) {

    private val dataStore = context.dataStore

    val userSessionFlow: Flow<UserSession> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserSession(preferences)
        }

    private fun mapUserSession(preferences: Preferences): UserSession {
        val token = preferences[UserPreferencesKeys.AUTH_TOKEN]
        val userId = preferences[UserPreferencesKeys.USER_ID]
        val userName = preferences[UserPreferencesKeys.USER_NAME]
        val userEmail = preferences[UserPreferencesKeys.USER_EMAIL]
        val userUsername = preferences[UserPreferencesKeys.USER_USERNAME]
        val userRole = preferences[UserPreferencesKeys.USER_ROLE]
        val isLoggedIn = preferences[UserPreferencesKeys.IS_LOGGED_IN] ?: false

        val user = if (userId != null && userName != null && userEmail != null && userUsername != null && userRole != null) {
            UserDto(id = userId, name = userName, email = userEmail, username = userUsername, role = userRole)
        } else {
            null
        }
        return UserSession(token, user, isLoggedIn)
    }


    suspend fun saveUserSession(token: String, user: UserDto) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.AUTH_TOKEN] = token
            preferences[UserPreferencesKeys.USER_ID] = user.id
            preferences[UserPreferencesKeys.USER_NAME] = user.name
            preferences[UserPreferencesKeys.USER_EMAIL] = user.email
            preferences[UserPreferencesKeys.USER_USERNAME] = user.username
            preferences[UserPreferencesKeys.USER_ROLE] = user.role
            preferences[UserPreferencesKeys.IS_LOGGED_IN] = true
        }
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.AUTH_TOKEN)
            preferences.remove(UserPreferencesKeys.USER_ID)
            preferences.remove(UserPreferencesKeys.USER_NAME)
            preferences.remove(UserPreferencesKeys.USER_EMAIL)
            preferences.remove(UserPreferencesKeys.USER_USERNAME)
            preferences.remove(UserPreferencesKeys.USER_ROLE)
            preferences[UserPreferencesKeys.IS_LOGGED_IN] = false
            // Or simply: preferences.clear() if you want to wipe everything in this DataStore
        }
    }

    // Optional: Functions to get individual values if needed, though userSessionFlow is often preferred
    suspend fun getAuthToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[UserPreferencesKeys.AUTH_TOKEN]
        }.first() // Use first() to get a single value directly
    }

    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[UserPreferencesKeys.IS_LOGGED_IN] ?: false
        }.first()
    }
}
