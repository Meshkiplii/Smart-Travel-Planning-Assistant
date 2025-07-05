package com.meshkipli.smarttravel.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.R


// --- Data Models ---
data class LocationInfo(
    @DrawableRes val imageRes: Int,
    val title: String,
    val price: String
)

// --- Reusable Components ---

@Composable
fun LocationCard(location: LocationInfo) {
    Card(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = location.imageRes),
                contentDescription = location.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 20.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(4) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("from", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            location.price,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    IconButton(
                        onClick = { /* Handle favorite */ },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF9882B))
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Favorite",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}


// --- Main Map Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val orangeColor = Color(0xFFF9882B)
    var searchText by remember { mutableStateOf("") }

    val locations = listOf(
        LocationInfo(R.drawable.img_sunset_avenue, "Sunset evening avenue", "$299 / night"),
        LocationInfo(R.drawable.img_forest_view, "Misty Forest Lodge", "$249 / night")
    )

    // In a real app, these would come from a dynamic data source
    val mapPins = listOf(
        Pair(80.dp, 200.dp), Pair(180.dp, 220.dp), Pair(140.dp, 300.dp),
        Pair(240.dp, 320.dp), Pair(290.dp, 250.dp), Pair(120.dp, 400.dp),
        Pair(200.dp, 450.dp), Pair(310.dp, 420.dp), Pair(320.dp, 520.dp)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Map Layer
        Image(
            painter = painterResource(id = R.drawable.img_map_background),
            contentDescription = "Map Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Map Pins Layer
        mapPins.forEach { (x, y) ->
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Map Pin",
                tint = orangeColor,
                modifier = Modifier
                    .size(40.dp)
                    .absoluteOffset(x = x, y = y)
            )
        }

        // 3. Top Floating UI Layer
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                IconButton(onClick = { /* Handle back press */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }

            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
            }

            Surface(
                shape = CircleShape,
                color = orangeColor,
                shadowElevation = 6.dp
            ) {
                IconButton(onClick = { /* Handle filter */ }) {
                    Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color.White)
                }
            }
        }

        // 4. Bottom Content Layer
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Text(
                "Location targeting",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(locations) { location ->
                    LocationCard(location = location)
                }
            }
        }
    }
}


// --- Preview ---
@Preview(showBackground = true, name = "Map Screen", widthDp = 360, heightDp = 800)
@Composable
fun MapScreenPreview() {
    MaterialTheme {
        MapScreen()
    }
}