package com.meshkipli.smarttravel.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meshkipli.smarttravel.data.remote.TourDto
import com.meshkipli.smarttravel.data.repository.NetworkResult
import com.meshkipli.smarttravel.data.repository.TourRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TripsUiState(
    val isLoading: Boolean = false,
    val tours: List<TourDto> = emptyList(),
    val error: String? = null
)

class TripsViewModel(
    private val tourRepository: TourRepository = TourRepository() // Inject this in a real app
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripsUiState(isLoading = true))
    val uiState: StateFlow<TripsUiState> = _uiState

    init {
        fetchTours()
    }

    fun fetchTours() {
        viewModelScope.launch {
            _uiState.value = TripsUiState(isLoading = true, tours = _uiState.value.tours) // Keep old tours while loading new
            when (val result = tourRepository.getAllTours()) {
                is NetworkResult.Success -> {
                    _uiState.value = TripsUiState(isLoading = false, tours = result.data.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = TripsUiState(
                        isLoading = false,
                        error = "Error ${result.code.value}: ${result.message}",
                        tours = emptyList() // Clear tours on error or keep old ones
                    )
                }
                is NetworkResult.Exception -> {
                    _uiState.value = TripsUiState(
                        isLoading = false,
                        error = "Exception: ${result.e.localizedMessage ?: "An unexpected error occurred"}",
                        tours = emptyList() // Clear tours on error
                    )
                    result.e.printStackTrace()
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
