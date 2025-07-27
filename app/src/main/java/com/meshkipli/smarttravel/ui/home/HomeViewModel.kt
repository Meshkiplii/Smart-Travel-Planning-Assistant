package com.meshkipli.smarttravel.ui.home // Or your ViewModel package

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meshkipli.smarttravel.data.local.UserPreferencesRepository
import com.meshkipli.smarttravel.data.local.UserSession // Make sure this is accessible
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application.applicationContext)

    // Expose only the user's name as a StateFlow for the HomeScreen
    val userName: StateFlow<String> = userPreferencesRepository.userSessionFlow
        .map { userSession ->
            userSession.user?.name ?: "Guest" // Default to "Guest" if name is not available
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Loading..." // Initial value while DataStore is loading
        )

    // Alternatively, if you want to expose the whole UserSession object:
     val userSession: StateFlow<UserSession?> = userPreferencesRepository.userSessionFlow
         .stateIn(
             scope = viewModelScope,
             started = SharingStarted.WhileSubscribed(5000),
             initialValue = null // Or UserSession(null, null, false)
         )

    // If you need logout functionality accessible from HomeScreen
     fun logout() {
         viewModelScope.launch {
             userPreferencesRepository.clearUserSession()
             // Here you might navigate to Login screen or update some other app-wide state
         }
     }
}
