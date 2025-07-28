package com.meshkipli.smarttravel.ui.tourdetails

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material3.RichText // Use Material3 RichText
import com.meshkipli.smarttravel.ScheduleTripActivity
import com.meshkipli.smarttravel.data.remote.TourDto
import com.meshkipli.smarttravel.data.repository.TourRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourDetailsScreen(
    tourId: String,
    onNavigateBack: () -> Unit,
    onPlanTripClicked: (tourId: String, title: String, description: String?, coverImageUrl: String?) -> Unit,
    // Example of providing the factory. Better to use Hilt or a DI graph.
    viewModelFactory: TourDetailsViewModelFactory = TourDetailsViewModelFactory(TourRepository())
) {
    val viewModel: TourDetailsViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.tour?.title ?: "Tour Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Error: ${uiState.error}")
                }
            }
            uiState.tour != null -> {
                TourDetailsContent(tour = uiState.tour!!,   onPlanTripClicked = {
                    // Pass necessary details to the TripPlannerScreen
                    onPlanTripClicked(
                        tourId, // or tour.id if available and preferred
                        uiState.tour!!.title,
                        uiState.tour!!.description, // Description
                        uiState.tour!!.coverImage // Cover Image URL
                    )
                }, modifier = Modifier.padding(paddingValues))
            }
            else -> {
                 Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Tour data not available.")
                }
            }
        }
    }
}

@Composable
fun TourDetailsContent(tour: TourDto,onPlanTripClicked: () -> Unit , modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val orangeColor = Color(0xFFF9882B)
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = tour.coverImage,
            contentDescription = tour.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = tour.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Destination: ${tour.destination.name}",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "From $${tour.pricePerPerson} / person",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFF9882B) // Use your theme color if possible
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "About this tour:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Use RichText for rendering HTML/Markdown from tour.about
        // Make sure tour.about contains valid Markdown or HTML that RichText can parse.
        // If it's pure HTML, you might need to use a different RichText parser if Markdown doesn't work.
        // For HTML, you might need a library that converts HTML to an AnnotatedString or use WebView.
        // commonmark-richtext is for Markdown.
        Surface(
            tonalElevation = 1.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(16.dp)){
                RichText(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Assuming tour.about is Markdown.
                    // If it's HTML, you might need to preprocess it or use a different library
                    // or WebView for complex HTML. RichText primarily focuses on Markdown.
                    Markdown(content = tour.about ?: "No details available.")
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Plan Trip Button
        Button(
            onClick = {
                onPlanTripClicked() // Call the new callback
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

        // Add more details as needed (e.g., itinerary, includes, etc.)
    }
}
