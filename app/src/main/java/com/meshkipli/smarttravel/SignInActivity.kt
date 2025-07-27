package com.meshkipli.smarttravel

import SignInViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.meshkipli.smarttravel.data.repository.AuthRepository
import com.meshkipli.smarttravel.ui.auth.SignInScreen
import com.meshkipli.smarttravel.ui.auth.SignInViewModel
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme


class SignInActivity : ComponentActivity() {
    private lateinit var signInViewModel: SignInViewModel // Instantiate ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTravelTheme {
                val context = LocalContext.current
                val uiState by signInViewModel.uiState.collectAsState()

                SignInScreen(
                    onNavigateBack = { finish() },
                    onSignInWithEmail = { email, password ->
                        signInViewModel.signInUser(email, password)
                    },
                    onSignInWithGoogle = {
                        Toast.makeText(context, "Google Sign-In not implemented yet.", Toast.LENGTH_SHORT).show()
                        println("Sign in with Google")
                    },
                    onSignInWithFacebook = {
                        Toast.makeText(context, "Facebook Sign-In not implemented yet.", Toast.LENGTH_SHORT).show()
                        println("Sign in with Facebook")
                    },
                    onNavigateToSignUp = {
                        val intent = Intent(context, SignUpActivity::class.java)
                        startActivity(intent)
                        // finish() // Optional: finish SignInActivity if you don't want it in back stack
                    },
                    isLoading = uiState.isLoading,
                    signInError = uiState.error
                )

                // Observe login success or error for side effects
                LaunchedEffect(uiState) {
                    if (uiState.loginSuccess) {
                        Toast.makeText(context, "Login Successful! Welcome ${uiState.user?.name ?: ""}", Toast.LENGTH_LONG).show()
                        // TODO: Navigate to HomeActivity or your main app screen
                        val intent = Intent(this@SignInActivity, HomeActivity::class.java) // Assuming HomeActivity exists
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
                        startActivity(intent)
                        finish() // Finish SignInActivity
                    }
                    // Show toast for error, only if it hasn't been "consumed"
                    uiState.error?.let { errorMsg ->
                        if (!errorMsg.startsWith("Consumed")) { // Avoid re-showing if error message is just "Consumed"
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            signInViewModel.clearError() // Optional: clear error in ViewModel after showing it
                        }
                    }
                }
            }
        }
        val authRepository = AuthRepository() // Replace with actual instantiation

        // 2. Create the ViewModelFactory
        val viewModelFactory = SignInViewModelFactory(application, authRepository)

        // 3. Get the ViewModel instance using the factory
        signInViewModel = ViewModelProvider(this, viewModelFactory).get(SignInViewModel::class.java)

    }
}


