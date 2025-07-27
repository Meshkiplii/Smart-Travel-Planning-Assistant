package com.meshkipli.smarttravel.ui.tourdetails

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.createSavedStateHandle
    import androidx.lifecycle.viewmodel.CreationExtras
    import com.meshkipli.smarttravel.data.repository.TourRepository

    class TourDetailsViewModelFactory(
        private val tourRepository: TourRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(TourDetailsViewModel::class.java)) {
                val savedStateHandle = extras.createSavedStateHandle()
                return TourDetailsViewModel(savedStateHandle, tourRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
