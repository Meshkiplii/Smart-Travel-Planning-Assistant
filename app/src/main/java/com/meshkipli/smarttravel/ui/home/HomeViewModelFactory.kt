package com.meshkipli.smarttravel.ui.home

import android.app.Application import androidx.lifecycle.ViewModel import androidx.lifecycle.ViewModelProvider import com.meshkipli.smarttravel.data.repository.TourRepository import com.meshkipli.smarttravel.ui.home.HomeViewModel  class HomeViewModelFactory(
      private val application: Application,
      private val tourRepository: TourRepository
  ) : ViewModelProvider.Factory { // Use ViewModelProvider.Factory directly for more control

      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
          if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
              return HomeViewModel(application, tourRepository) as T
          }
          throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
      }
  }