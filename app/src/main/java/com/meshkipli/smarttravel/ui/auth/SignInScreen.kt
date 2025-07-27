package com.meshkipli.smarttravel.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.R
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean = false,
    // isPasswordField: Boolean = false, // This parameter is less direct than visualTransformation
    visualTransformation: VisualTransformation = VisualTransformation.None // Ensure this is present and used
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholderText, color = Color.Gray) },
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null, tint = Color.Gray) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFE0E0E0),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color(0xFFFAFAFA),
            unfocusedContainerColor = Color(0xFFFAFAFA),
        ),
        singleLine = true,
        isError = isError,
        visualTransformation = visualTransformation // This applies the transformation
        // keyboardOptions = if (visualTransformation is PasswordVisualTransformation) { // Optional: Set keyboard type
        //    KeyboardOptions(keyboardType = KeyboardType.Password)
        // } else {
        //    KeyboardOptions.Default
        // }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onNavigateBack: () -> Unit,
    onSignInWithEmail: (email: String, password: String) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInWithFacebook: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    isLoading: Boolean, // Added
    signInError: String? // Added
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val orangeColor = Color(0xFFF9882B) // Your brand color

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Sign in",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))

                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholderText = "Enter e-mail address",
                    leadingIcon = Icons.Outlined.Email,
                    isError = signInError != null // Show error state on email if general error exists
                )
                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholderText = "Enter password",
                    leadingIcon = Icons.Outlined.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = signInError != null // Show error state on password if general error exists
                )

                // Display API error message if any
                signInError?.let {
                    // Avoid showing toast again if error is cleared or on configuration change if not handled
                    if (!it.startsWith("Consumed")) {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(32.dp))
                OrContinueWithDivider()
                Spacer(modifier = Modifier.height(32.dp))
                SocialLoginButton(
                    text = "Continue with Google",
                    iconPainter = painterResource(id = R.drawable.ic_google_logo), // Ensure these drawables exist
                    backgroundColor = Color.Black,
                    contentColor = Color.White,
                    onClick = onSignInWithGoogle,
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(16.dp))
                SocialLoginButton(
                    text = "Continue with Facebook",
                    iconPainter = painterResource(id = R.drawable.ic_facebook_logo), // Ensure these drawables exist
                    backgroundColor = Color(0xFF3B5998),
                    contentColor = Color.White,
                    onClick = onSignInWithFacebook,
                    enabled = !isLoading
                )
            } // End of scrollable column

            Button(
                onClick = {
                    // Basic validation (can be more robust)
                    if (email.isNotBlank() && password.isNotBlank()) {
                        onSignInWithEmail(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                TextButton(onClick = onNavigateToSignUp, enabled = !isLoading) {
                    Text(
                        "Sign Up",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = orangeColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // For bottom padding
        }
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    iconPainter: Painter,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true // Added enabled state
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = iconPainter,
                contentDescription = "$text logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

@Composable
fun OrContinueWithDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
        Text(
            "Or continue with",
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
    }
}
@Preview(showBackground = true, name = "Sign In Screen Preview", widthDp = 360, heightDp = 780)
@Composable
fun SignInScreenPreview() {
    SmartTravelTheme {
        SignInScreen(
            onNavigateBack = {},
            onSignInWithEmail = { _, _ -> },
            onSignInWithGoogle = {},
            onSignInWithFacebook = {},
            onNavigateToSignUp = {},
            isLoading = false,
            signInError = null
        )
    }
}

@Preview(showBackground = true, name = "Sign In Screen Loading Preview", widthDp = 360, heightDp = 780)
@Composable
fun SignInScreenLoadingPreview() {
    SmartTravelTheme {
        SignInScreen(
            onNavigateBack = {},
            onSignInWithEmail = { _, _ -> },
            onSignInWithGoogle = {},
            onSignInWithFacebook = {},
            onNavigateToSignUp = {},
            isLoading = true,
            signInError = null
        )
    }
}

@Preview(showBackground = true, name = "Sign In Screen Error Preview", widthDp = 360, heightDp = 780)
@Composable
fun SignInScreenErrorPreview() {
    SmartTravelTheme {
        SignInScreen(
            onNavigateBack = {},
            onSignInWithEmail = { _, _ -> },
            onSignInWithGoogle = {},
            onSignInWithFacebook = {},
            onNavigateToSignUp = {},
            isLoading = false,
            signInError = "Invalid email or password."
        )
    }
}