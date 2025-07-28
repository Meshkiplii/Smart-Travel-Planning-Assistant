package com.meshkipli.smarttravel.ui.trips

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.R
import com.meshkipli.smarttravel.ScheduleTripActivity


// --- Reusable Components ---

@Composable
fun RatingBar(rating: Float, reviewCount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$rating",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "($reviewCount reviews)",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "See reviews",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.clickable { /* Handle click */ }
        )
    }
}

@Composable
fun PricingRow(
    icon: ImageVector,
    iconBgColor: Color,
    title: String,
    price: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconBgColor.copy(alpha = 0.5f).compositeOver(Color.Black), // Auto-contrast color
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(price, color = Color.Gray, fontSize = 14.sp)
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Details",
            tint = Color.Gray
        )
    }
}


// --- Main Trip Screen ---

@Composable
fun TripDetailsScreen(onNavigateBack: () -> Unit) {
    // --- Edge-to-edge effect setup ---
    val view = LocalView.current
    val context = LocalContext.current
//    val window = (view.context as Activity).window
//    WindowCompat.setDecorFitsSystemWindows(window, false)
//    window.statusBarColor = Color.Transparent.toArgb()
    val orangeColor = Color(0xFFF9882B)
    val currentTripTitle = "Nordic Cottage" // Example

    Box(modifier = Modifier.fillMaxSize()) { // Overall container

        // --- Layer for Image and its directly associated Text ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp) // Height of the image area
        ) {
            // 1. Background Image
            Image(
                painter = painterResource(id = R.drawable.img_nordic_cottage),
                contentDescription = "Trip Background",
                modifier = Modifier.fillMaxSize(), // Fill this inner Box
                contentScale = ContentScale.Crop
            )

            // 2. Gradient Scrim Layer (for text readability over the image)
            Box(
                modifier = Modifier
                    .fillMaxSize() // Fill this inner Box, same size as image
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 300f, // Adjust gradient start to ensure text is readable
                            endY = 480f   // Gradient ends at the bottom of the image
                        )
                    )
            )

            // 3. Title Text (Aligned within this inner Box, relative to the Image)
            Text(
                text = currentTripTitle,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 50.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart) // Align to BottomStart of this inner Box
                    .padding(start = 24.dp, bottom = 120.dp) // Small margin from image edges
            )
        }

        // 3. Main Scrollable Content Layer
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            // This spacer pushes the white sheet down, revealing the image
            Spacer(modifier = Modifier.height(380.dp))

            // White Content Sheet
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .padding(top = 24.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text("About Bail", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Blue Lagoon Drive from Reykjavík, the capital of Iceland, to the southeast for about an houryou can reach Blue Lagoon, the famous",
                        color = Color.Gray,
                        lineHeight = 24.sp,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RatingBar(rating = 4.79f, reviewCount = 78)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Pricing", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Pricing Items
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    PricingRow(
                        icon = Icons.Default.Flight,
                        iconBgColor = Color(0xFFFFEBEE), // Light Pink
                        title = "Flights",
                        price = "from $199"
                    )
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    PricingRow(
                        icon = Icons.Default.Apartment,
                        iconBgColor = Color(0xFFE3F2FD), // Light Blue
                        title = "Hotels",
                        price = "from $199 / night"
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))

                // Plan Trip Button
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orangeColor)
                ) {
                    Text("Plan trip", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp)) // Padding at the end of the scroll
            }
        }

        // 4. Floating UI Layer (On top of everything)
        // Back Arrow
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

    }
}

// --- Preview ---

@Preview(showBackground = true, name = "Trip Details Screen", widthDp = 360, heightDp = 800)
@Composable
fun TripDetailsScreenPreview() {
    MaterialTheme {
        TripDetailsScreen(onNavigateBack = {})
    }
}

// Helper function for color compositing
fun Color.compositeOver(background: Color): Color {
    val alpha = this.alpha
    if (alpha == 1.0f) {
        return this
    }
    val r = (this.red * alpha) + (background.red * (1.0f - alpha))
    val g = (this.green * alpha) + (background.green * (1.0f - alpha))
    val b = (this.blue * alpha) + (background.blue * (1.0f - alpha))
    return Color(r, g, b, 1.0f)
}