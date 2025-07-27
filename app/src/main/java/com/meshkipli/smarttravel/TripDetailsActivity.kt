package com.meshkipli.smarttravel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meshkipli.smarttravel.ui.trips.TripDetailsScreen
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme

class TripDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTravelTheme {
                TripDetailsScreen(
                    onNavigateBack = {
                        finish()
                    }
                )
            }
        }
    }
}