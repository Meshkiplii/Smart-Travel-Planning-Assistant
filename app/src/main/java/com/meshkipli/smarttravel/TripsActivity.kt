package com.meshkipli.smarttravel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meshkipli.smarttravel.screens.TripsScreen
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme

class TripsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTravelTheme {
                TripsScreen (
                    onNavigateBack = {
                        finish()
                    }
                )
            }
        }
    }
}