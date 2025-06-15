package com.meshkipli.smarttravel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// A placeholder for your actual drawable resources
// In a real project, you would have these drawables in your res/drawable folder
object R {
    object drawable {
        // Replace with your actual vector drawables
        const val ic_apple_logo = 0
        const val ic_facebook_logo = 0
    }
}


// --- Reusable Components ---

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    leadingIcon: ImageVector,
    isPasswordField: Boolean = false
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
        visualTransformation = if (isPasswordField) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None
    )
}

@Composable
fun SocialLoginButton(
    text: String,
    iconResId: Int,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // In a real app, use painterResource(id = iconResId)
            // Using a placeholder icon as resource is not available.
            Icon(
                imageVector = if (text.contains("Apple")) Icons.Default.ChatBubble else Icons.Default.ChatBubble,
                contentDescription = text,
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

// --- Screen Implementations ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen() {
    var email by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { /* Handle back press */ }) {
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
                modifier = Modifier.weight(1f)
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
                    leadingIcon = Icons.Outlined.Email
                )
                Spacer(modifier = Modifier.height(32.dp))
                OrContinueWithDivider()
                Spacer(modifier = Modifier.height(32.dp))
                SocialLoginButton(
                    text = "Continue with Apple",
                    iconResId = R.drawable.ic_apple_logo,
                    backgroundColor = Color.Black,
                    contentColor = Color.White,
                    onClick = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                SocialLoginButton(
                    text = "Continue with Facebook",
                    iconResId = R.drawable.ic_facebook_logo,
                    backgroundColor = Color(0xFF3B5998),
                    contentColor = Color.White,
                    onClick = {}
                )
            }

            Button(
                onClick = { /* Handle Continue */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9882B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen() {
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
                    IconButton(onClick = { /* Handle back press */ }) {
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
                    .verticalScroll(rememberScrollState()) // Make content scrollable if needed
            ) {
                Text(
                    "Sign up free",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))

                // NOTE: The label "Or continue with" is repeated in the design.
                // Replicating it here, but in a real app, this would be unusual UX.
                Text("Or continue with", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholderText = "Enter e--mail address",
                    leadingIcon = Icons.Outlined.Email
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text("Or continue with", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholderText = "Create a password",
                    leadingIcon = Icons.Outlined.Lock,
                    isPasswordField = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text("Or continue with", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField(
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    placeholderText = "Repeat password",
                    leadingIcon = Icons.Outlined.Lock,
                    isPasswordField = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { termsAccepted = !termsAccepted }
                ) {
                    // This is a custom checkbox-like icon from the design
                    Icon(
                        imageVector = Icons.Filled.ChatBubble,
                        contentDescription = "Terms of Service",
                        tint = if (termsAccepted) orangeColor else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
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
                                .firstOrNull()?.let {
                                    // Handle Terms of Service click
                                }
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Handle Continue */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


// --- Previews ---

@Preview(showBackground = true, name = "Sign In Screen", widthDp = 360, heightDp = 780)
@Composable
fun SignInScreenPreview() {
    MaterialTheme {
        SignInScreen()
    }
}

@Preview(showBackground = true, name = "Sign Up Screen", widthDp = 360, heightDp = 780)
@Composable
fun SignUpScreenPreview() {
    MaterialTheme {
        SignUpScreen()
    }
}