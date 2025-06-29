package com.meshkipli.smarttravel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import com.meshkipli.smarttravel.screens.ScheduleTripScreen
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme

class ScheduleTripActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // val tripTitle = intent.getStringExtra("trip_title")

        setContent {
            SmartTravelTheme {
                ScheduleTripScreen(
                    // tripTitle = tripTitle,
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}

