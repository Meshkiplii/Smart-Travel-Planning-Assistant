package com.meshkipli.smarttravel.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {
    val AUTH_TOKEN = stringPreferencesKey("auth_token")
    val USER_ID = stringPreferencesKey("user_id")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_USERNAME = stringPreferencesKey("user_username") // Added username
    val USER_ROLE = stringPreferencesKey("user_role") // Added role
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in") // To quickly check login status
}
