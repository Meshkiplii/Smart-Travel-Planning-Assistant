package com.meshkipli.smarttravel.ui // Or your appropriate package

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meshkipli.smarttravel.data.local.UserPreferencesRepository
import com.meshkipli.smarttravel.data.local.UserSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application.applicationContext)

    // Expose the user session as a StateFlow
    val userSession: StateFlow<UserSession?> = userPreferencesRepository.userSessionFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keep subscribed for 5s after last collector
            initialValue = null // Or a default UserSession(null, null, false)
        )

    // Example of how you might check login status immediately
    // val isLoggedIn: StateFlow<Boolean> = userPreferencesRepository.userSessionFlow
    //     .map { it.isLoggedIn }
    //     .stateIn(viewModelScope, SharingStarted.Eagerly, false)
}