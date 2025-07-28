package com.meshkipli.smarttravel.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material3.RichText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlannerScreen(
    tripTitle: String?,
    tripDescription: String?,
    coverImageUrl: String?, // Added coverImageUrl parameter
    onNavigateBack: () -> Unit,
    viewModel: TripPlannerViewModel = viewModel()
) {
    val orangeColor = Color(0xFFF9882B)
    val uiState by viewModel.uiState.collectAsState()
    var showDateRangePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = if (uiState.startDate.isNotEmpty()) {
            try { dateFormatter.parse(uiState.startDate)?.time } catch (e: Exception) { null }
        } else null,
        initialSelectedEndDateMillis = if (uiState.endDate.isNotEmpty()) {
            try { dateFormatter.parse(uiState.endDate)?.time } catch (e: Exception) { null }
        } else null,
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= Calendar.getInstance().get(Calendar.YEAR)
            }
        }
    )

    val selectedDateText = remember(uiState.startDate, uiState.endDate) {
        if (uiState.startDate.isNotEmpty() && uiState.endDate.isNotEmpty()) {
            if (uiState.startDate == uiState.endDate) {
                uiState.startDate
            } else {
                "${uiState.startDate} - ${uiState.endDate}"
            }
        } else if (uiState.startDate.isNotEmpty()) {
            "${uiState.startDate} - (Select End Date)"
        } else {
            "Select Dates"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plan: ${tripTitle ?: "Your Trip"}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // --- Trip Info Section ---
            if (!coverImageUrl.isNullOrBlank() || !tripTitle.isNullOrBlank() || !tripDescription.isNullOrBlank()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    if (!coverImageUrl.isNullOrBlank()) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(coverImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = tripTitle ?: "Trip cover image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            loading = {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.errorContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Image load failed",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (!tripTitle.isNullOrBlank()) {
                        Text(
                            text = tripTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (!tripDescription.isNullOrBlank()) {
                        Text(
                            text = tripDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // --- Planning Fields Section ---
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Set Up Your Adventure",
                    style = MaterialTheme.typography.titleLarge, // Changed from fontSize
                    fontWeight = FontWeight.Bold,
                    color = orangeColor
                )

                OutlinedTextField(
                    value = selectedDateText,
                    onValueChange = { /* Read only */ },
                    label = { Text("Trip Dates") },
                    leadingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDateRangePicker = true }) {
                            Icon(Icons.Filled.Edit, "Select Dates")
                        }
                    }
                )

                OutlinedTextField(
                    value = uiState.numberOfPeople,
                    onValueChange = { viewModel.updateNumberOfPeople(it) },
                    label = { Text("Number of People") },
                    leadingIcon = { Icon(Icons.Filled.Group, contentDescription = "People") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.budget,
                    onValueChange = { viewModel.updateBudget(it) },
                    label = { Text("Budget (e.g., 500)") },
                    leadingIcon = { Icon(Icons.Filled.MonetizationOn, contentDescription = "Budget") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.generateAiTripPlan(tripTitle ?: "Unknown Trip", tripDescription)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoadingAiSuggestion && uiState.startDate.isNotEmpty() && uiState.endDate.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                ) {
                    if (uiState.isLoadingAiSuggestion) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "AI Plan", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate AI Suggestion", fontSize = 18.sp, color = Color.White)
                    }
                }

                uiState.aiError?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(onClick = { viewModel.clearAiError() }) {
                        Text("Dismiss Error")
                    }
                }

                uiState.aiSuggestion?.let { suggestion ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "AI Suggestion:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {

                        RichText(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // The Markdown composable parses and prepares the content
                            Markdown(
                                content = suggestion,
                                // You can provide custom renderers here if needed
                                // commonmarkFeatures = CommonmarkAstNodeParser.DEFAULT_FEATURES,
                                // onLinkClicked = { /* handle link clicks */ }
                            )
                        }
                    }
                    TextButton(onClick = { viewModel.clearAiSuggestion() }) {
                        Text("Clear Suggestion")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Add some padding at the bottom
            }
        }

        if (showDateRangePicker) {
            DatePickerDialog(
                onDismissRequest = { showDateRangePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDateRangePicker = false
                            dateRangePickerState.selectedStartDateMillis?.let {
                                viewModel.updateStartDate(dateFormatter.format(Date(it)))
                            }
                            dateRangePickerState.selectedEndDateMillis?.let {
                                viewModel.updateEndDate(dateFormatter.format(Date(it)))
                            } ?: run {
                                // If end date is null but start date is selected,
                                // and if you want to set end date to be the same as start:
                                if (dateRangePickerState.selectedStartDateMillis != null) {
                                    viewModel.updateEndDate(viewModel.uiState.value.startDate)
                                } else {
                                    viewModel.updateEndDate("") // Or clear it
                                }
                            }
                        },
                        enabled = dateRangePickerState.selectedStartDateMillis != null
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDateRangePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier.weight(1f),
                    title = {
                        Text(
                            "Select Trip Dates",
                            modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp, bottom = 16.dp)
                        )
                    },
                    headline = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 12.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    dateRangePickerState.selectedStartDateMillis?.let {
                                        dateFormatter.format(Date(it))
                                    } ?: "Start Date",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text("-", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 8.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    dateRangePickerState.selectedEndDateMillis?.let {
                                        dateFormatter.format(Date(it))
                                    } ?: "End Date",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    },
                    showModeToggle = true
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp", name = "Trip Planner Full")
@Composable
fun TripPlannerScreenFullPreview() {
    MaterialTheme { // Ensure you have a MaterialTheme defined in your app
        TripPlannerScreen(
            tripTitle = "Amazing Bali Villa Getaway",
            tripDescription = "Experience the serene beauty of Bali in this luxurious villa with a private pool and stunning rice paddy views. Perfect for relaxation and adventure seeking individuals looking for a memorable experience.",
            coverImageUrl = "https://example.com/placeholder_image.jpg", // Replace with a valid placeholder for preview
            onNavigateBack = {}
            // viewModel will be default
        )
    }
}
