package com.meshkipli.smarttravel.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meshkipli.smarttravel.data.remote.RegisterRequest
import com.meshkipli.smarttravel.data.remote.RegisterResponse
import com.meshkipli.smarttravel.data.repository.AuthRepository
import com.meshkipli.smarttravel.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// State for the UI to observe
data class SignUpUiState(
    val isLoading: Boolean = false,
    val registrationSuccess: Boolean = false,
    val error: String? = null,
    val token: String? = null,
    val userName: String? = null
)

class SignUpViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() { // Provide AuthRepository via DI in a real app

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun signUpUser(email: String, name: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignUpUiState(isLoading = true) // Set loading state

            val request = RegisterRequest(email = email, name = name, password = password)
            when (val result = authRepository.registerUser(request)) {
                is NetworkResult.Success -> {
                    val responseData = result.data
                    _uiState.value = SignUpUiState(
                        isLoading = false,
                        registrationSuccess = true,
                        token = responseData.token,
                        userName = responseData.user.name
                    )
                    // You might want to save the token and user info here (e.g., DataStore, SharedPreferences)
                    println("Registration successful! Token: ${responseData.token}, User: ${responseData.user.name}")
                }
                is NetworkResult.Error -> {
                    _uiState.value = SignUpUiState(
                        isLoading = false,
                        error = "Error ${result.code.value}: ${result.message}"
                    )
                    println("Registration error: ${result.code} - ${result.message}")
                }
                is NetworkResult.Exception -> {
                    _uiState.value = SignUpUiState(
                        isLoading = false,
                        error = "Exception: ${result.e.localizedMessage ?: "An unexpected error occurred"}"
                    )
                    println("Registration exception: ${result.e.message}")
                    result.e.printStackTrace() // Log the stack trace for debugging
                }
            }
        }
    }
}
