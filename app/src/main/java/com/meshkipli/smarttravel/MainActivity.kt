package com.meshkipli.smarttravel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Import this
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen() // Call this before super.onCreate()

        super.onCreate(savedInstanceState)

        // Optional: Keep the splash screen visible for longer if needed (e.g., for data loading)
        // splashScreen.setKeepOnScreenCondition { /* your condition, e.g., !viewModel.isDataLoaded */ true }
        // If you use setKeepOnScreenCondition, make sure it eventually becomes false to dismiss the splash screen.

        setContent {
            SmartTravelTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartTravelTheme {
        Greeting("Android")
    }
}