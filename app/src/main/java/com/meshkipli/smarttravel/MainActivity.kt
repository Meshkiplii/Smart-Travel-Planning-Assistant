package com.meshkipli.smarttravel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Import this
import androidx.lifecycle.lifecycleScope
import com.meshkipli.smarttravel.ui.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)


        finish()
        lifecycleScope.launch {
            mainViewModel.userSession.collectLatest { userSession ->
                if (userSession == null) {
                    // Still loading preferences or first time
                    return@collectLatest
                }

                if (userSession.isLoggedIn && userSession.token != null) {
                    // User is logged in, token is available
                    // Navigate to HomeActivity
                    Log.d("MainActivity", "User logged in: ${userSession.user?.name}, Token: ${userSession.token}")
                    // Intent to HomeActivity, finish Splash/Login
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // User is not logged in or token missing
                    // Navigate to SignInActivity
                    Log.d("MainActivity", "User not logged in. Navigating to SignIn.")
                    val intent = Intent(this@MainActivity, OnBoardingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}

