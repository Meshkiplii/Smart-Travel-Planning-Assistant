package com.meshkipli.smarttravel

import android.app.Application
import com.meshkipli.smarttravel.data.local.db.AppDatabase
import com.meshkipli.smarttravel.data.repository.ItineraryRepository

class SmartTravelApplication : Application() {
    // Using by lazy so the database and repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this) }
    val itineraryRepository by lazy {
        ItineraryRepository(database.itineraryDayDao(), database.itineraryActivityDao())
    }
}