package com.meshkipli.smarttravel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme // Assuming you have this

// Consider moving AuthTextField to a common file if not already done
// For brevity, I'm assuming it's available (e.g., copied from SignInActivity or a common file)
// If not, copy AuthTextField composable here as well.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onSignUp: (email: String, pass: String) -> Unit,
    onTermsClicked: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    val orangeColor = Color(0xFFF9882B)

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
                .padding(bottom = 24.dp)
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

                Text("Or continue with", color = Color.Gray, style = MaterialTheme.typography.bodyMedium) // Design shows this label multiple times
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField( // Make sure AuthTextField is accessible here
                    value = email,
                    onValueChange = { email = it },
                    placeholderText = "Enter e-mail address",
                    leadingIcon = Icons.Outlined.Email
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text("Or continue with", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField( // Make sure AuthTextField is accessible here
                    value = password,
                    onValueChange = { password = it },
                    placeholderText = "Create a password",
                    leadingIcon = Icons.Outlined.Lock,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text("Or continue with", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField( // Make sure AuthTextField is accessible here
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    placeholderText = "Repeat password",
                    leadingIcon = Icons.Outlined.Lock,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(24.dp))

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
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (password == repeatPassword && termsAccepted) {
                        onSignUp(email, password)
                    } else {
                        // Handle error: passwords don't match or terms not accepted
                        println("Sign up error: Passwords match: ${password == repeatPassword}, Terms accepted: $termsAccepted")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                shape = RoundedCornerShape(12.dp),
                enabled = termsAccepted // Only enable if terms are accepted
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTravelTheme { // Replace with your actual theme
                SignUpScreen(
                    onNavigateBack = { finish() }, // Finishes SignUpActivity
                    onSignUp = { email, pass ->
                        // Handle sign up logic
                        println("Sign up with Email: $email, Pass: $pass")
                        // Navigate to next screen (e.g. MainActivity or SignInActivity)
                    },
                    onTermsClicked = {
                        // Handle "Terms of Service" click, e.g., open a WebView or another screen
                        println("Terms of Service clicked!")
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Sign Up Screen Preview", widthDp = 360, heightDp = 780)
@Composable
fun SignUpScreenPreview() {
    SmartTravelTheme {
        SignUpScreen({}, { _, _ -> }, {})
    }
}