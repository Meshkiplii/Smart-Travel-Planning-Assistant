package com.meshkipli.smarttravel

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme


@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPasswordField: Boolean = false,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
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
        visualTransformation = visualTransformation
    )
}

@Composable
fun SocialLoginButton(
    text: String,
    iconPainter: Painter,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
        )
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onNavigateBack: () -> Unit,
    onSignInWithEmail: (email: String) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInWithFacebook: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }

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
                    text = "Continue with Google",
                    iconPainter = painterResource(id = R.drawable.ic_google_logo),
                    backgroundColor = Color.Black,
                    contentColor = Color.White,
                    onClick = onSignInWithGoogle
                )
                Spacer(modifier = Modifier.height(16.dp))
                SocialLoginButton(
                    text = "Continue with Facebook",
                    iconPainter = painterResource(id = R.drawable.ic_facebook_logo),
                    backgroundColor = Color(0xFF3B5998),
                    contentColor = Color.White,
                    onClick = onSignInWithFacebook
                )
            }

            Button(
                onClick = { onSignInWithEmail(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9882B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
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
                TextButton(onClick = onNavigateToSignUp) {
                    Text(
                        "Sign Up",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(id = R.color.primary) // <-- Corrected: Use colorResource()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        }
    }


class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTravelTheme {
                val context = LocalContext.current
                SignInScreen(
                    onNavigateBack = { finish() },
                    onSignInWithEmail = { email ->
                        println("Sign in with email: $email")
                    },
                    onSignInWithGoogle = {
                        println("Sign in with Google")
                    },
                    onSignInWithFacebook = {
                        println("Sign in with Facebook")
                    },
                    onNavigateToSignUp = {
                        val intent = Intent(context, SignUpActivity::class.java)
                         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }

                )
            }
        }
    }
}


@Preview(showBackground = true, name = "Sign In Screen Preview", widthDp = 360, heightDp = 780)
@Composable
fun SignInScreenPreview() {
    SmartTravelTheme {
        SignInScreen(
            onNavigateBack = {},
            onSignInWithEmail = {},
            onSignInWithGoogle = {},
            onSignInWithFacebook = {},
            onNavigateToSignUp = {}
        )
    }
}