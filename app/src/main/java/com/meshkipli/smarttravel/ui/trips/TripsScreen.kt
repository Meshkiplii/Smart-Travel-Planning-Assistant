package com.meshkipli.smarttravel.ui.trips

// import androidx.annotation.DrawableRes // Not needed if using URLs
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.res.painterResource // Keep if you have other local images
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage // Import Coil
import com.meshkipli.smarttravel.data.remote.TourDto // Import your DTO

// --- Data Models from API (already defined in TourDtos.kt) ---
// No need for mock TripLocation data class or getMockTrips() anymore

@Composable
fun TourItemCard(tour: TourDto, onClick: () -> Unit) { // Renamed and adapted
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(IntrinsicSize.Min), // Allow children to fill height
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = tour.coverImage,
                contentDescription = tour.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp) // Keep a fixed width or use aspect ratio
                    .fillMaxHeight() // Fill the height of the Row
                    .clip(RoundedCornerShape(16.dp)),
                // Optional: Add placeholder and error drawables
                // placeholder = painterResource(R.drawable.placeholder_image),
                // error = painterResource(R.drawable.error_image)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = tour.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                // Rating and review count are not directly in your TourDto.
                // You might need to derive or omit this, or add it to TourDto if available.
                // For now, let's show destination as an example or omit.
                Text(
                    text = "Destination: ${tour.destination.name}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                // Example of showing a star if featured (adapt as needed)
                if (tour.isFeatured) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "Featured",
                            tint = Color(0xFFFFC107), // Gold color
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Featured", fontSize = 13.sp, color = Color.Gray)
                    }
                }

                Text(
                    text = tour.description, // Or a snippet from tour.about after parsing HTML
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "from $${tour.pricePerPerson} / person", // Assuming price is per person
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF9882B)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTourDetails: (String) -> Unit,
    tripsViewModel: TripsViewModel = viewModel() // Inject ViewModel
) {
    var searchText by remember { mutableStateOf("") }
    val uiState by tripsViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle errors with a Toast (or a more sophisticated Snackbar)
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            tripsViewModel.clearError() // Clear error after showing
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = { Text("Trips", fontWeight = FontWeight.Bold) },
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
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it }, // TODO: Implement filtering based on searchText
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
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.tours.isEmpty()) { // Show loading indicator only if no data yet
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.tours.isEmpty() && !uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No trips found.")
            }
        } else {
            // Filtered list based on search text (simple client-side filter)
            val filteredTours = remember(searchText, uiState.tours) {
                if (searchText.isBlank()) {
                    uiState.tours
                } else {
                    uiState.tours.filter {
                        it.title.contains(searchText, ignoreCase = true) ||
                                it.description.contains(searchText, ignoreCase = true) ||
                                it.destination.name.contains(searchText, ignoreCase = true)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredTours, key = { tour -> tour.id }) { tour ->
                    TourItemCard(tour = tour, onClick = {
                        onNavigateToTourDetails(tour.id) // Use the callback
                    })
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Trips Screen - Loading", widthDp = 360, heightDp = 800)
@Composable
fun TripsScreenLoadingPreview() {
    MaterialTheme {
        // For preview, you'd ideally mock the ViewModel to return a loading state
        // This direct call won't easily show the loading state without a running ViewModel
        // that's actually loading. You could create a fake ViewModel for previews.
        TripsScreen(onNavigateBack = {}, onNavigateToTourDetails = {})
        // To force loading state in preview, you might pass a specific uiState
        // This requires TripsScreen to accept uiState as a parameter, which adds complexity
        // for simple previews. For now, rely on ViewModel default.
    }
}

@Preview(showBackground = true, name = "Trips Screen - With Data", widthDp = 360, heightDp = 800)
@Composable
fun TripsScreenWithDataPreview() {
    MaterialTheme {
        // To preview with data, you'd mock the ViewModel to provide sample TourDto list.
        // For simplicity, this will show "No trips found" or try to fetch if network is available
        // in some preview environments (less reliable).
        TripsScreen(onNavigateBack = {}, onNavigateToTourDetails = {})
    }
}

// You can remove the old TripsScreenPreview if it relied on mock data.
