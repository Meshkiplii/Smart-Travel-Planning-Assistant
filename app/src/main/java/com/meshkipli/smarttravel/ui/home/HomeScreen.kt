package com.meshkipli.smarttravel.ui.home

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.meshkipli.smarttravel.MapActivity
import com.meshkipli.smarttravel.R
import com.meshkipli.smarttravel.TripDetailsActivity
import com.meshkipli.smarttravel.data.remote.DestinationDto
import com.meshkipli.smarttravel.data.remote.TourDto
import com.meshkipli.smarttravel.data.repository.TourRepository


data class Location(
    val id: String? = null,
    @DrawableRes val imageRes: Int = 0,
    val imageUrl: String? = null, // Nullable
    val title: String,            // Non-nullable
    val price: String? = null,    // Nullable
    val rating: Double? = null,   // Nullable
    val subtitle: String? = null  // Nullable
)

fun mapTourDtoToLocation(tourDto: TourDto): Location {
    return Location(
        id = tourDto.id,
        imageUrl = tourDto.coverImage, // Use the image URL from TourDto
        title = tourDto.title,
        subtitle = tourDto.destination.name,
         price = tourDto.pricePerPerson?.let { "from $$it" }
//         rating = 4.0
    )
}

fun mapDestinationDtoToLocation(destinationDto: DestinationDto): Location {
    return Location(
        imageUrl = destinationDto.coverImage,
        title = destinationDto.name,
        // subtitle = "${destinationDto.region}, ${destinationDto.country}" // Example subtitle
        subtitle = destinationDto.country ?: destinationDto.region ?: "" // More robust subtitle
        // price and rating are likely not available in DestinationDto, so they'll be null
    )
}
@Composable
fun LocationCard(location: Location, onNavigateToTourDetails: (String) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .width(180.dp)
            .height(240.dp)
            .clickable { // Make the whole Card clickable
                if (location.id != null) {
                    onNavigateToTourDetails(location.id)
                }

            },

        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (location.imageUrl != null) {
                AsyncImage(
                    model = location.imageUrl,
                    contentDescription = location.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
//                    placeholder = painterResource(id = R.drawable.img_placeholder), // Optional placeholder
//                    error = painterResource(id = R.drawable.img_placeholder) // Optional error image
                )
            } else if (location.imageRes != 0) {
                Image(
                    painter = painterResource(id = location.imageRes),
                    contentDescription = location.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback for when no image is available
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)) {
                    Text("No Image", modifier = Modifier.align(Alignment.Center))
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = 300f,
                            endY = 600f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(
                        text = location.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (location.price != null && location.rating != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "from ${location.price}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = location.rating.toString(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFFFC107), // Yellow for star
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(start = 4.dp)
                                )
                            }
                        }
                    } else if (location.subtitle != null) {
                        Text(
                            text = location.subtitle,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun LocationCategoryRow(
    title: String,
    locations: List<Location>,
    onNavigateToTourDetails: (String) -> Unit,
    onViewMoreClicked: (() -> Unit)? = null // Add a callback for "View More"
) {
    val orangeColor = Color(0xFFF9882B) // Define your orange color

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically, // Align items vertically
            horizontalArrangement = Arrangement.SpaceBetween // Space title and "View More"
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                // Optional: if title can be very long, prevent it from pushing "View More" too much
                // modifier = Modifier.weight(1f),
                // maxLines = 1,
                // overflow = TextOverflow.Ellipsis
            )
            if (onViewMoreClicked != null) { // Only show if a click action is provided
                Text(
                    text = "View More",
                    color = orangeColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onViewMoreClicked) // Make it clickable
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(locations) { location ->
                LocationCard(location = location,  onNavigateToTourDetails = onNavigateToTourDetails)
            }
        }
    }
}

// --- Main Screen ---

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onNavigateToTrips: () -> Unit,
    onNavigateToTourDetails: (String) -> Unit,
    // How you get tourRepository instance here depends on your setup
    // For simplicity, creating it directly here, but ideally from a DI container or graph
    homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            LocalContext.current.applicationContext as Application,
            TourRepository() // Or get it from a proper DI source
        )
    )
) {
    // --- Dummy Data ---
 
//    val popularLocations2 = listOf(
//        Location(R.drawable.img_western_strait, "Western Strait", subtitle = "16 locations"),
//        Location(R.drawable.img_beach_house, "Beach House", subtitle = "22 locations"),
//        Location(R.drawable.img_mountain_view, "Mountain View", subtitle = "36 locations")
//    )
    val context = LocalContext.current
    val orangeColor = Color(0xFFF9882B)
    var searchText by remember { mutableStateOf("") }
    val uiState by homeViewModel.uiState.collectAsState()
    val popularLocationsFromApi = uiState.popularTours.map { tourDto ->
        mapTourDtoToLocation(tourDto) // Your existing mapper function
    }
    val destinationLocations = uiState.destinations.map { mapDestinationDtoToLocation(it) }
    Scaffold(
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()) // Adjust for bottom bar
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Header
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Find your next trip",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Text(
                    text = uiState.userName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            // Search and Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50), // Fully rounded
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color(0xFFFAFAFA),
                        unfocusedContainerColor = Color(0xFFFAFAFA),
                    )
                )
                // Filter Button
                IconButton(
                    onClick = {  val intent = Intent(context, MapActivity::class.java)
                        context.startActivity(intent)},
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(orangeColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filter",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Location Sections
            if (uiState.isLoadingDestinations) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.destinationsError?.let { error ->
                // Display error for destinations
                Text("Error loading destinations: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 20.dp))
            }
            if (!uiState.isLoadingDestinations && uiState.destinationsError == null && destinationLocations.isNotEmpty()) {
                LocationCategoryRow(
                    title = "Destinations",
                    locations = destinationLocations,
                    onNavigateToTourDetails = onNavigateToTourDetails
                    // Use the mapped destinations
                    // onViewMoreClicked = { /* Optional: Navigate to a "all destinations" screen */ }
                )
            } else if (!uiState.isLoadingDestinations && uiState.destinationsError == null && destinationLocations.isEmpty()) {
                Text("No destinations found.", modifier = Modifier.padding(horizontal = 20.dp))
            }
//            LocationCategoryRow(title = "Destinations", locations = popularLocations1)
            Spacer(modifier = Modifier.height(24.dp))
//            LocationCategoryRow(title = "Popular Locations", locations = popularLocations2 ,
//                onViewMoreClicked = {
////                    val intent = Intent(context, TripsActivity::class.java)
////                    context.startActivity(intent)
//                    onNavigateToTrips()
//                })

            if (uiState.isLoadingPopularTours) {
                CircularProgressIndicator()
            }

            uiState.popularToursError?.let { error ->
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            }

            if (!uiState.isLoadingPopularTours && uiState.popularToursError == null) {
                LocationCategoryRow(
                    title = "Popular Locations",
                    locations = popularLocationsFromApi,
                    onNavigateToTourDetails = onNavigateToTourDetails,
                    onViewMoreClicked = {
//                    val intent = Intent(context, TripsActivity::class.java)
//                    context.startActivity(intent)
                        onNavigateToTrips()
                    }
                    // ...

                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Preview ---

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(onNavigateToTrips = {}, onNavigateToTourDetails = {})
    }
}