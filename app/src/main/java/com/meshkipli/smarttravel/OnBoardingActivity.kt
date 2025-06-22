package com.meshkipli.smarttravel

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager // Import HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState // Import rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch // Import launch for coroutine scope

// Data class to represent the content of each onboarding screen
data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    val title: String,
    val subtitle: String
)

// Main App Theme (so we can use Material3 components) - Assuming this is defined elsewhere or here
@Composable
fun OnboardingScreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme( // Example colors
            primary = Color(0xFFF9882B),
            background = Color.White,
            onBackground = Color.Black,
            onSurfaceVariant = Color.Gray
        ),
        typography = Typography(), // Assuming you have Typography defined
        content = content
    )
}

// Reusable composable for a single onboarding screen content (without FAB)
@Composable
fun OnboardingScreenPageContent(page: OnboardingPage, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .padding(top = 40.dp, bottom = 20.dp), // Adjust overall vertical padding
        horizontalAlignment = Alignment.CenterHorizontally,
        // Remove Arrangement.SpaceBetween to have more control with Spacers or weights
    ) {
        // Give the image section a specific proportion of the screen or a max height
        Box(
            modifier = Modifier
                // .weight(0.6f) // Option A: Assign a weight, e.g., 60% of available space
                .fillMaxWidth() // Ensure it can use the width
                .heightIn(max = 300.dp) // Option B: Set a maximum height (adjust dp as needed)
                .aspectRatio(1f, matchHeightConstraintsFirst = true), // Maintain aspect ratio within constraints
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = page.imageRes),
                contentDescription = page.title,
                modifier = Modifier
                    .fillMaxSize(0.9f), // Fill 90% of the constrained Box
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(32.dp)) // Adjust spacing as needed

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // Modifier.weight(0.4f) // If you used weight for the Box, text can take the rest
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
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

        // Add a flexible spacer at the bottom if you want to push text up
        // when there's extra space (e.g., on very tall screens)
        // This is only effective if the Column itself isn't using Arrangement.SpaceBetween
        Spacer(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalFoundationApi::class) // For HorizontalPager
@Composable
fun OnboardingPagerScreen(onboardingPages: List<OnboardingPage>, onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Get context for starting activity

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingScreenPageContent(page = onboardingPages[pageIndex])
        }

        // FAB at the bottom, aligned to center
        FloatingActionButton(
            onClick = {
                if (pagerState.currentPage < onboardingPages.size - 1) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    // Last page: Navigate to LoginActivity
                    onFinish()
                }
            },
            containerColor = Color(0xFFF9882B),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomCenter) // Align FAB to bottom center
                .padding(bottom = 60.dp) // Adjust padding as needed
                .size(72.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_step_next),
                contentDescription = "Next",
                modifier = Modifier.size(36.dp)
            )
        }

        // Optional: Add page indicators (dots)
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp) // Position above/below FAB as you like
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(onboardingPages.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(color, CircleShape)
                        .size(12.dp)
                )
            }
        }
    }
}


class OnBoardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sample onboarding pages (ensure these drawables exist)
        val onboardingPages = listOf(
            OnboardingPage(
                imageRes = R.drawable.traveling_ill, // Replace with your actual drawable
                title = "Make your own private\ntravel plan",
                subtitle = "Formulate your strategy to receive\nwonderful gift packs"
            ),
            OnboardingPage(
                imageRes = R.drawable.sitting_ill, // Replace with your actual drawable
                title = "Customize your\nHigh-end travel",
                subtitle = "Countless high-end\nentertainment facilities"
            ),
            OnboardingPage(
                imageRes = R.drawable.beach_ill, // Replace with your actual drawable
                title = "High-end leisure projects\nto choose from",
                subtitle = "The world's first-class modern leisure\nand entertainment method"
            )
        )

        setContent {
            OnboardingScreenTheme {
                OnboardingPagerScreen(
                    onboardingPages = onboardingPages,
                    onFinish = {
                        // Navigate to LoginActivity
                        val intent = Intent(this@OnBoardingActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish() // Finish OnBoardingActivity so user can't go back to it
                    }
                )
            }
        }
    }
}

// Preview for OnboardingPagerScreen
@Preview(showBackground = true)
@Composable
fun OnboardingPagerScreenPreview() {
    val previewPages = listOf(
        OnboardingPage(R.drawable.traveling_ill, "Title 1", "Subtitle 1"),
        OnboardingPage(R.drawable.sitting_ill, "Title 2", "Subtitle 2"),
        OnboardingPage(R.drawable.beach_ill, "Title 3", "Subtitle 3")
    )
    OnboardingScreenTheme {
        OnboardingPagerScreen(onboardingPages = previewPages, onFinish = { /* Preview doesn't navigate */ })
    }
}