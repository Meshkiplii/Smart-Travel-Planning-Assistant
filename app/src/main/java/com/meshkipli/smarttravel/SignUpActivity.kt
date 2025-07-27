package com.meshkipli.smarttravel

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.ui.auth.SignUpViewModel
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme // Assuming you have this

// In SignUpScreen composable (SignUpActivity.kt)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onSignUp: (email: String, name: String, pass: String) -> Unit, // Added name parameter
    onTermsClicked: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    isLoading: Boolean, // Added for loading indicator
    signUpError: String? // Added for displaying errors
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") } // Add state for name
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    val orangeColor = Color(0xFFF9882B)
    var showPasswordError by remember { mutableStateOf<String?>(null) }

    // ... (Scaffold and TopAppBar as before) ...
    Scaffold( /* ... */) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp) // Ensure this doesn't overlap with system bars if needed
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Sign up free",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Email Field
                    AuthTextField( // Assuming AuthTextField is a custom composable you have
                        value = email,
                        onValueChange = { email = it },
                        placeholderText = "Enter e-mail address",
                        leadingIcon = Icons.Outlined.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp)) // Reduced spacer

                    // Name Field
                    AuthTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholderText = "Enter your name",
                        leadingIcon = Icons.Outlined.Person
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    AuthTextField(
                        value = password,
                        onValueChange = { password = it; showPasswordError = null },
                        placeholderText = "Create a password",
                        leadingIcon = Icons.Outlined.Lock,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Repeat Password Field
                    AuthTextField(
                        value = repeatPassword,
                        onValueChange = { repeatPassword = it; showPasswordError = null },
                        placeholderText = "Repeat password",
                        leadingIcon = Icons.Outlined.Lock,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = showPasswordError != null
                    )

                    showPasswordError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ... (Terms and Conditions Row as before) ...
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { termsAccepted = !termsAccepted }
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = orangeColor,
                                uncheckedColor = Color.Gray,
                                checkmarkColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val annotatedString = buildAnnotatedString {
                            append("I have read the ")
                            pushStringAnnotation(tag = "TOS", annotation = "terms_of_service_url")
                            withStyle(style = SpanStyle(color = orangeColor, fontWeight = FontWeight.Bold)) {
                                append("Terms of Service")
                            }
                            pop()
                        }
                        ClickableText(
                            text = annotatedString,
                            onClick = { offset ->
                                annotatedString.getStringAnnotations(tag = "TOS", start = offset, end = offset)
                                    .firstOrNull()?.let { onTermsClicked() }
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                    }
                } // End of scrollable column

                Spacer(modifier = Modifier.height(16.dp))

                // Display API error message if any
                signUpError?.let {
                    if(!it.startsWith("Consumed")) { // Avoid showing already handled errors
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }


                Button(
                    onClick = {
                        if (password != repeatPassword) {
                            showPasswordError = "Passwords do not match."
                        } else if (!termsAccepted) {
                            showPasswordError = "Please accept the Terms of Service."
                        }
                        else {
                            showPasswordError = null
                            onSignUp(email, name, password) // Pass name here
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading && termsAccepted // Disable button when loading or terms not accepted
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // ... (Sign In navigation Row as before) ...
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Already have an account? ", // Changed text
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    TextButton(onClick = onNavigateToSignIn) {
                        Text(
                            "Sign In",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = orangeColor // Using your orangeColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Optional: for bottom padding
            } // End of main column

            // Optional: Full screen loading overlay
            // if (isLoading && signUpError == null) { // Show only if loading and no specific field error shown above
            //     Box(
            //         modifier = Modifier
            //             .fillMaxSize()
            //             .background(Color.Black.copy(alpha = 0.3f))
            //             .clickable(enabled = false, onClick = {}), // Consume clicks
            //         contentAlignment = Alignment.Center
            //     ) {
            //         CircularProgressIndicator(color = orangeColor)
            //     }
            // }
        }
    }
}



class SignUpActivity : ComponentActivity() {
    // Instantiate ViewModel using activity-ktx delegate
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTravelTheme {
                val context = LocalContext.current
                val uiState by signUpViewModel.uiState.collectAsState()

                SignUpScreen(
                    onNavigateBack = { finish() },
                    onSignUp = { email, name, password -> // Assuming SignUpScreen takes name now
                        signUpViewModel.signUpUser(email, name, password)
                    },
                    onTermsClicked = {
                        println("Terms of Service clicked!")
                        Toast.makeText(context, "Terms of Service clicked!", Toast.LENGTH_SHORT).show()
                    },
                    onNavigateToSignIn = {
                        val intent = Intent(context, SignInActivity::class.java) // Assuming SignInActivity exists
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        finish() // Finish SignUpActivity
                    },
                    isLoading = uiState.isLoading, // Pass loading state to UI
                    signUpError = uiState.error // Pass error state to UI
                )

                // Observe registration success or error for side effects (e.g., navigation, Toast)
                LaunchedEffect(uiState) {
                    if (uiState.registrationSuccess) {
                        Toast.makeText(context, "Registration Successful! Welcome ${uiState.userName}", Toast.LENGTH_LONG).show()
                        // Navigate to another screen (e.g., Home or SignIn to use the new account)
                        // Example: Navigate to SignIn to then log in with the new credentials
                        val intent = Intent(context, SignInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        finish() // Finish SignUpActivity
                    }
                    uiState.error?.let {
                        // Avoid showing toast again if error is cleared or on configuration change if not handled
                        if (!it.startsWith("Consumed")) { // Simple check to avoid re-showing if error message is just "Consumed"
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                        // Optionally clear the error in ViewModel after showing it once
                        // signUpViewModel.clearError()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Sign Up Screen Preview", widthDp = 360, heightDp = 780)
@Composable
fun SignUpScreenPreview() {
    SmartTravelTheme {
        SignUpScreen(
            {}, { _, _, _ -> /* email, name, password */}, {},
            onNavigateToSignIn = {},
            isLoading = false,
            signUpError = null

        )
    }
}