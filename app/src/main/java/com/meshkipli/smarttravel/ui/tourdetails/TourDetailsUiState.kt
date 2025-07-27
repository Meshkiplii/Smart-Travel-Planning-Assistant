package com.meshkipli.smarttravel.ui.tourdetails // Create this package

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meshkipli.smarttravel.data.remote.TourDto // Your TourDto
import com.meshkipli.smarttravel.data.repository.TourRepository // Assuming you have a repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TourDetailsUiState(
    val tour: TourDto? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class TourDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val tourRepository: TourRepository // Inject your repository
) : ViewModel() {

    private val tourId: String = savedStateHandle.get<String>("tourId")!! // Get tourId from navigation

    private val _uiState = MutableStateFlow(TourDetailsUiState())
    val uiState: StateFlow<TourDetailsUiState> = _uiState.asStateFlow()

    init {
        fetchTourDetails(tourId)
    }

    private fun fetchTourDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = TourDetailsUiState(isLoading = true)
            try {
                // In a real app, you might fetch from a local cache first, then network
                // For simplicity, let's assume tourRepository.getTourById(id) fetches the full DTO
                val tour = tourRepository.getTourById(id) // Implement this in your repository
                if (tour != null) {
                    _uiState.value = TourDetailsUiState(tour = tour, isLoading = false)
                } else {
                    _uiState.value = TourDetailsUiState(error = "Tour not found", isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = TourDetailsUiState(error = "Failed to load tour details: ${e.localizedMessage}", isLoading = false)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
