package com.meshkipli.smarttravel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// A placeholder for your actual drawable resources
// In a real project, you would have these drawables in your res/drawable folder


// Data class to represent the content of each onboarding screen
data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    val title: String,
    val subtitle: String
)

// Main App Theme (so we can use Material3 components)
@Composable
fun OnboardingScreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color.White,
            onBackground = Color.Black,
            onSurfaceVariant = Color.Gray
        ),
        typography = Typography(),
        content = content
    )
}

// Reusable composable for a single onboarding screen
@Composable
fun OnboardingScreen(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(812.dp) // Typical screen height for preview
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Distributes space between elements
    ) {
        // Top Image
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Note: Ensure your drawable resource includes the yellow blob background
            Image(
                painter = painterResource(id = page.imageRes),
                contentDescription = page.title,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f), // Maintain a square-like aspect ratio
                contentScale = ContentScale.Fit
            )
        }

        // Middle Text Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Bottom Action Button
        FloatingActionButton(
            onClick = { /* Handle button click */ },
            containerColor = Color(0xFFF9882B), // Specific orange from the image
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(72.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_step_next), // Correct
                contentDescription = "Next",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

// Preview composable to display all three screens
@Preview(showBackground = true)
@Composable
fun OnboardingScreensPreview() {
    // Dummy data for the preview
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.traveling_ill,
            title = "Make your own private\ntravel plan",
            subtitle = "Formulate your strategy to receive\nwonderful gift packs"
        ),
        OnboardingPage(
            imageRes = R.drawable.sitting_ill,
            title = "Customize your\nHigh-end travel",
            subtitle = "Countless high-end\nentertainment facilities"
        ),
        OnboardingPage(
            imageRes = R.drawable.beach_ill,
            title = "High-end leisure projects\nto choose from",
            subtitle = "The world's first-class modern leisure\nand entertainment method"
        )
    )

    OnboardingScreenTheme {
        // The Column is scrollable to show all screens in the preview
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            pages.forEach { page ->
                // This is a placeholder as previews don't have real resources
                // In a real app, you'd use a HorizontalPager to swipe between them.
                val imageToDisplay = when (page.title.first()) {
                    'M' -> R.drawable.traveling_ill
                    'C' -> R.drawable.sitting_ill
                    else -> R.drawable.beach_ill
                }
                // In a real app with resources, you'd just use page.imageRes
                OnboardingScreen(page = page.copy(imageRes = imageToDisplay))
                // Add a divider for better separation in the preview
                if (page != pages.last()) {
                    Divider(color = Color.LightGray, thickness = 8.dp)
                }
            }
        }
    }
}

// This would be your main activity
class OnBoardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // In a real app, you would likely use a HorizontalPager from Accompanist
            // or the official Foundation library to create a swipeable onboarding flow.
            // For this example, we'll just show the first screen.
            val firstPage = OnboardingPage(
                imageRes = R.drawable.traveling_ill,
                title = "Make your own private\ntravel plan",
                subtitle = "Formulate your strategy to receive\nwonderful gift packs"
            )
            OnboardingScreenTheme {
                OnboardingScreen(page = firstPage)
            }
        }
    }
}