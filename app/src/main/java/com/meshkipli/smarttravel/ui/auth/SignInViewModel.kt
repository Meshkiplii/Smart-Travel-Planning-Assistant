package com.meshkipli.smarttravel.ui.auth

import android.app.Application // Import Application
import androidx.lifecycle.AndroidViewModel // Change to AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meshkipli.smarttravel.data.local.UserPreferencesRepository
import com.meshkipli.smarttravel.data.remote.LoginRequest
import com.meshkipli.smarttravel.data.remote.UserDto
import com.meshkipli.smarttravel.data.repository.AuthRepository
import com.meshkipli.smarttravel.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignInUiState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val error: String? = null,
    val token: String? = null,
    val user: UserDto? = null
)

// Change ViewModel to AndroidViewModel to get Application context
class SignInViewModel(
    application: Application, // Add application parameter
    private val authRepository: AuthRepository
) : AndroidViewModel(application) { // Pass application to superclass

    // Instantiate UserPreferencesRepository using the application context
    private val userPreferencesRepository = UserPreferencesRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignInUiState(isLoading = true)

            val request = LoginRequest(email = email, password = password)
            when (val result = authRepository.loginUser(request)) {
                is NetworkResult.Success -> {
                    val responseData = result.data
                    userPreferencesRepository.saveUserSession(responseData.token, responseData.user)

                    _uiState.value = SignInUiState(
                        isLoading = false,
                        loginSuccess = true,
                    )
                    println("Login successful! Token and user info saved. User: ${responseData.user.name}")
                }
                is NetworkResult.Error -> {
                    _uiState.value = SignInUiState(
                        isLoading = false,
                        error = "Error ${result.code.value}: ${result.message}"
                    )
                    println("Login error: ${result.code} - ${result.message}")
                }
                is NetworkResult.Exception -> {
                    _uiState.value = SignInUiState(
                        isLoading = false,
                        error = "Exception: ${result.e.localizedMessage ?: "An unexpected error occurred"}"
                    )
                    println("Login exception: ${result.e.message}")
                    result.e.printStackTrace()
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, isLoading = false)
    }

    fun logoutUser() {
        viewModelScope.launch {
            userPreferencesRepository.clearUserSession()
            _uiState.value = SignInUiState(loginSuccess = false)
            println("User logged out, session cleared.")
        }
    }
}
