package com.meshkipli.smarttravel.ui.itinerary 

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

    
    private val _selectedDayId = MutableStateFlow<Long?>(null)
    val selectedDayId: StateFlow<Long?> = _selectedDayId.asStateFlow()

    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class) 
    val activitiesForSelectedDay: StateFlow<List<ItineraryActivity>> =
        _selectedDayId.flatMapLatest { dayId ->
            if (dayId != null && dayId != 0L) { 
                repository.getActivitiesForDay(dayId)
            } else {
                flowOf(emptyList()) 
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )


    fun selectDay(dayId: Long?) { 
        _selectedDayId.value = dayId
    }

    
    fun addItineraryDay(dayLabel: String, date: String) = viewModelScope.launch {
        repository.insertDay(ItineraryDay(dayLabel = dayLabel, date = date))
    }

    fun updateItineraryDay(day: ItineraryDay) = viewModelScope.launch {
        repository.updateDay(day)
    }

    fun deleteItineraryDay(day: ItineraryDay) = viewModelScope.launch {
        repository.deleteDay(day)
        
        if (_selectedDayId.value == day.id) {
            _selectedDayId.value = null
        }
    }

    
    fun addItineraryActivity(dayId: Long, time: String, name: String, emoji: String?) = viewModelScope.launch {
        
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


class ItineraryViewModelFactory(private val repository: ItineraryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItineraryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItineraryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
