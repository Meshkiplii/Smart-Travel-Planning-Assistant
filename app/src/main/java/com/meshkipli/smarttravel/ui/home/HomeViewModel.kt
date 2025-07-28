package com.meshkipli.smarttravel.ui.home // Or your ViewModel package

import android.app.Application
import androidx.compose.animation.core.copy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meshkipli.smarttravel.data.local.UserPreferencesRepository
import com.meshkipli.smarttravel.data.remote.DestinationDto
// import com.meshkipli.smarttravel.data.local.UserSession // Already imported
import com.meshkipli.smarttravel.data.remote.TourDto // Import TourDto
import com.meshkipli.smarttravel.data.repository.NetworkResult // Import NetworkResult
import com.meshkipli.smarttravel.data.repository.TourRepository // Import TourRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Define HomeUiState here or in a separate file if preferred
data class HomeUiState(
    val isLoadingPopularTours: Boolean = false,
    val popularTours: List<TourDto> = emptyList(),
    val popularToursError: String? = null,
    // Destinations state - NEW
    val isLoadingDestinations: Boolean = false,
    val destinations: List<DestinationDto> = emptyList(),
    val destinationsError: String? = null,
    val userName: String = "Loading..." // Initial value for username
    // You can also include the full UserSession if needed by the UI directly
    // val userSession: UserSession? = null
)

class HomeViewModel(
    application: Application,
    // Inject TourRepository. In a real app, use Hilt or Koin for DI.
    private val tourRepository: TourRepository = TourRepository()
) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application.applicationContext)

    // Internal state for tour fetching logic
    private val _tourState = MutableStateFlow(
        HomeUiState(isLoadingPopularTours = true) // Set initial loading for tours
    )


    // Combine user session data and tour data into a single HomeUiState
    val uiState: StateFlow<HomeUiState> = combine(
        userPreferencesRepository.userSessionFlow,
        _tourState
    ) { userSession, tourState ->
        tourState.copy(
            userName = userSession.user?.name ?: "Guest",
            // If you need the full session:
            // userSession = userSession
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(userName = "Loading...", isLoadingPopularTours = true) // Ensure initial state matches combined logic
    )
    private val _navigateToLogin = MutableSharedFlow<Unit>()
    val navigateToLogin: SharedFlow<Unit> = _navigateToLogin.asSharedFlow()


    init {
        fetchPopularTours()
        fetchDestinations()
    }

    fun fetchPopularTours() {
        viewModelScope.launch {
            _tourState.update { currentState ->
                currentState.copy(isLoadingPopularTours = true, popularToursError = null)
            }

            when (val result = tourRepository.getAllTours()) {
                is NetworkResult.Success -> {
                    _tourState.update { currentState ->
                        currentState.copy(
                            isLoadingPopularTours = false,
                            popularTours = result.data.data, // Adjust if your DTO structure is different
                            popularToursError = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _tourState.update { currentState ->
                        currentState.copy(
                            isLoadingPopularTours = false,
                            popularToursError = "Error ${result.code.value}: ${result.message}"
                        )
                    }
                }
                is NetworkResult.Exception -> {
                    _tourState.update { currentState ->
                        currentState.copy(
                            isLoadingPopularTours = false,
                            popularToursError = "Exception: ${result.e.localizedMessage ?: "An unexpected error occurred"}"
                        )
                    }
                    result.e.printStackTrace() // Log the exception
                }
            }
        }
    }


    fun fetchDestinations() {
        viewModelScope.launch {
            _tourState.update { it.copy(isLoadingDestinations = true, destinationsError = null) }
            when (val result = tourRepository.getAllDestinations()) {
                is NetworkResult.Success -> {
                    _tourState.update {
                        it.copy(
                            isLoadingDestinations = false,
                            destinations = result.data.data // data contains List<DestinationDto>
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _tourState.update {
                        it.copy(
                            isLoadingDestinations = false,
                            destinationsError = "Error ${result.code.value}: ${result.message}"
                        )
                    }
                }
                is NetworkResult.Exception -> {
                    _tourState.update {
                        it.copy(
                            isLoadingDestinations = false,
                            destinationsError = "Exception: ${result.e.localizedMessage}"
                        )
                    }
                }
            }
        }
    }


    fun clearPopularToursError() {
        _tourState.update { currentState ->
            currentState.copy(popularToursError = null)
        }
    }

    // Logout functionality remains the same
    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearUserSession()
            // Optionally reset UI state related to user-specific data
            _tourState.value = HomeUiState(
                userName = "Guest", // Or some default logged-out state
                // Keep other states if they are not user-specific,
                // or reset them as needed.
                isLoadingPopularTours = false, // Or true if you want to reload on next login
                popularTours = emptyList(),    // Example: clear user-specific recommendations
                isLoadingDestinations = false,
                destinations = emptyList()
            )
            // Emit event to navigate
            _navigateToLogin.emit(Unit)
        }
    }
}
