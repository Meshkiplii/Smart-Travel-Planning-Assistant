package com.meshkipli.smarttravel.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.R



// --- Data Models ---
data class TripLocation(
    @DrawableRes val imageRes: Int,
    val name: String,
    val rating: Float,
    val reviewCount: Int,
    val price: Int,
    val description: String
)

// --- Mock Data ---
fun getMockTrips(): List<TripLocation> {
    return listOf(
        TripLocation(
            imageRes = R.drawable.img_trip_switzerland,
            name = "Alpine Chalet, Switzerland",
            rating = 4.9f,
            reviewCount = 128,
            price = 350,
            description = "A cozy retreat in the Swiss Alps, perfect for hiking and skiing."
        ),
        TripLocation(
            imageRes = R.drawable.img_trip_iceland,
            name = "Northern Lights, Iceland",
            rating = 4.8f,
            reviewCount = 210,
            price = 420,
            description = "Modern glass igloo with a panoramic view of the aurora borealis."
        ),
        TripLocation(
            imageRes = R.drawable.img_trip_greece,
            name = "Santorini Villa, Greece",
            rating = 4.9f,
            reviewCount = 340,
            price = 550,
            description = "Stunning white villa overlooking the Aegean Sea's blue waters."
        ),
        TripLocation(
            imageRes = R.drawable.img_trip_japan,
            name = "Kyoto Ryokan, Japan",
            rating = 4.7f,
            reviewCount = 98,
            price = 300,
            description = "Experience traditional Japanese hospitality with tatami floors and onsen."
        ),
        TripLocation(
            imageRes = R.drawable.img_trip_peru,
            name = "Sacred Valley, Peru",
            rating = 4.6f,
            reviewCount = 75,
            price = 280,
            description = "Lodge with breathtaking views of the Andes, near Machu Picchu."
        )
    )
}

@Composable
fun TripLocationCard(trip: TripLocation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* Handle card click */ }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = trip.imageRes),
                contentDescription = trip.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = trip.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${trip.rating} (${trip.reviewCount} reviews)",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = trip.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
                Text(
                    text = "from $${trip.price} / night",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF9882B)
                )
            }
        }
    }
}


// --- Main Trips Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(onNavigateBack: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    val tripLocations = getMockTrips()
    val orangeColor = Color(0xFFF9882B)


    Scaffold(
        containerColor = Color(0xFFF9F9F9), // Light background for contrast
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = { Text("Trips", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        // Show back button only if onNavigateBack is provided
                        if (onNavigateBack != null) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search your trips...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                    )
                )
            }
        },

    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tripLocations) { trip ->
                TripLocationCard(trip = trip)
            }
        }
    }
}


// --- Preview ---

@Preview(showBackground = true, name = "Trips Screen", widthDp = 360, heightDp = 800)
@Composable
fun TripsScreenPreview() {
    MaterialTheme {
        TripsScreen(onNavigateBack = {})
    }
}