package com.meshkipli.smarttravel.ui.itinerary // Correct package

import androidx.lifecycle.*
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryActivity
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryDay
import com.meshkipli.smarttravel.data.repository.ItineraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ItineraryViewModel(private val repository: ItineraryRepository) : ViewModel() {

    val allDays: StateFlow<List<ItineraryDay>> = repository.getAllDays()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // Keep track of the currently selected day's ID
    private val _selectedDayId = MutableStateFlow<Long?>(null)
    val selectedDayId: StateFlow<Long?> = _selectedDayId.asStateFlow()

    // Observe activities for the selected day
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class) // For flatMapLatest
    val activitiesForSelectedDay: StateFlow<List<ItineraryActivity>> =
        _selectedDayId.flatMapLatest { dayId ->
            if (dayId != null && dayId != 0L) { // Ensure dayId is valid
                repository.getActivitiesForDay(dayId)
            } else {
                flowOf(emptyList()) // Return empty list if no day is selected or ID is invalid
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )


    fun selectDay(dayId: Long?) { // Allow nullable dayId to clear selection
        _selectedDayId.value = dayId
    }

    // ItineraryDay CRUD
    fun addItineraryDay(dayLabel: String, date: String) = viewModelScope.launch {
        repository.insertDay(ItineraryDay(dayLabel = dayLabel, date = date))
    }

    fun updateItineraryDay(day: ItineraryDay) = viewModelScope.launch {
        repository.updateDay(day)
    }

    fun deleteItineraryDay(day: ItineraryDay) = viewModelScope.launch {
        repository.deleteDay(day)
        // If the deleted day was selected, clear the selection
        if (_selectedDayId.value == day.id) {
            _selectedDayId.value = null
        }
    }

    // ItineraryActivity CRUD
    fun addItineraryActivity(dayId: Long, time: String, name: String, emoji: String?) = viewModelScope.launch {
        // Ensure a day is selected or passed and is valid
        if (dayId != 0L) {
            repository.insertActivity(
                ItineraryActivity(
                    dayId = dayId,
                    time = time,
                    name = name,
                    emoji = emoji
                )
            )
        }
    }

    fun updateItineraryActivity(activity: ItineraryActivity) = viewModelScope.launch {
        repository.updateActivity(activity)
    }

    fun deleteItineraryActivity(activity: ItineraryActivity) = viewModelScope.launch {
        repository.deleteActivity(activity)
    }
}

// ViewModel Factory to provide the Repository to the ViewModel
class ItineraryViewModelFactory(private val repository: ItineraryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItineraryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItineraryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
