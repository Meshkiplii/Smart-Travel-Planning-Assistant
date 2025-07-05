package com.meshkipli.smarttravel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meshkipli.smarttravel.screens.MapScreen
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme

class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTravelTheme {
                MapScreen(
                    onNavigateBack = {
                        finish()
                    }
                )
            }
        }
    }
}