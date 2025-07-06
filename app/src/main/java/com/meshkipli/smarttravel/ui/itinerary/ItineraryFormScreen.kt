package com.meshkipli.smarttravel.ui.itinerary

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meshkipli.smarttravel.SmartTravelApplication
import com.meshkipli.smarttravel.data.local.db.dao.ItineraryActivityDao
import com.meshkipli.smarttravel.data.local.db.dao.ItineraryDayDao
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryActivity
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryDay
import com.meshkipli.smarttravel.data.repository.ItineraryRepository

// Data class for Timeline display (can be adapted or replaced)
data class DisplayableTimelineEvent(
    val time: String,
    val title: String,
    val subtitle: String, // Could be derived from activity details
    val icon: ImageVector,
    val isPast: Boolean = false, // You might need logic to determine this
    val originalActivity: ItineraryActivity? = null // To link back if needed
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryFormScreen(
    onNavigateToAddItinerary: () -> Unit, // <--- ADD THIS NAVIGATION LAMBDA
    itineraryViewModel: ItineraryViewModel = viewModel(
        factory = ItineraryViewModelFactory(
            (LocalContext.current.applicationContext as SmartTravelApplication).itineraryRepository
        )
    )
) {
    val orangeColor = Color(0xFFF9882B)
    val context = LocalContext.current

    // Observe days from the ViewModel
    val days by itineraryViewModel.allDays.collectAsState()
    // Observe activities for the selected day
    val activities by itineraryViewModel.activitiesForSelectedDay.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Categories - these can remain static or be fetched if they become dynamic
    val categories = listOf("Island", "Beach", "Resort") // Example categories
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }

    // Update selectedDayId in ViewModel when tab changes and days list is not empty
    LaunchedEffect(days, selectedTabIndex) {
        if (days.isNotEmpty() && selectedTabIndex < days.size) {
            itineraryViewModel.selectDay(days[selectedTabIndex].id)
        } else {
            itineraryViewModel.selectDay(null) // Clear selection if no days or index out of bounds
        }
    }

    // Transform ItineraryActivities into DisplayableTimelineEvents
    val timelineEvents = remember(activities) {
        activities.map { activity ->
            DisplayableTimelineEvent(
                time = activity.time,
                title = activity.name,
                subtitle = "Activity Details", // Replace with more meaningful subtitle if available
                icon = emojiToIcon(activity.emoji), // Helper function to map emoji to icon
                // isPast = determineIfPast(activity.time, days.getOrNull(selectedTabIndex)?.date) // Implement this logic
                originalActivity = activity
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guide", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = { // <--- ADD FAB HERE
            FloatingActionButton(
                onClick = {
                    onNavigateToAddItinerary()
                },
                containerColor = orangeColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Itinerary Day")
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Category Chips (can remain as is if static)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories.size) { index ->
                    val isSelected = categories[index] == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = categories[index] },
                        label = { Text(categories[index], fontWeight = FontWeight.SemiBold) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = orangeColor,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderWidth = 1.dp,
                            borderColor = Color.LightGray.copy(alpha = 0.5f),
                            selectedBorderColor = orangeColor,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            if (days.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Allow it to take up space if the list is empty
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No itinerary days yet.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                onNavigateToAddItinerary()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = orangeColor)
                        ) {
                            Text("Add Your First Itinerary Day", color = Color.White)
                        }
                    }
                }
            } else {
                // Day Tabs - Populated from ViewModel
                TabRow(
                    selectedTabIndex = selectedTabIndex.coerceIn(0, days.size -1), // Ensure index is valid
                    containerColor = Color.Transparent,
                    contentColor = Color.Black,
                    indicator = { tabPositions ->
                        if (tabPositions.isNotEmpty() && selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.coerceIn(0, tabPositions.size-1)]),
                                height = 3.dp,
                                color = orangeColor
                            )
                        }
                    }
                ) {
                    days.forEachIndexed { index, day ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    "${day.dayLabel}\n${day.date}", // Combine dayLabel and date
                                    lineHeight = 18.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) Color.Black else Color.Gray
                                )
                            },
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }

                // Timeline - Populated from ViewModel's activitiesForSelectedDay
                if (timelineEvents.isEmpty() && days.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No activities planned for this day yet.")
                    }
                } else {
                    Column(modifier = Modifier.padding(24.dp)) {
                        timelineEvents.forEachIndexed { index, event ->
                            TimelineNode(
                                event = event,
                                isLastNode = index == timelineEvents.lastIndex,
                                orangeColor = orangeColor,
                                 onClick = { event.originalActivity?.let { activity -> /* navigate to edit */ } }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to map emoji to a Material Icon (example)
// You'll need to expand this or use a more robust solution
fun emojiToIcon(emoji: String?): ImageVector {
    return when (emoji) {
        "🎉" -> Icons.Default.Celebration
        "✈️" -> Icons.Default.Flight
        "🏨" -> Icons.Default.Hotel
        "🍽️" -> Icons.Default.Restaurant
        "🗺️" -> Icons.Default.Map
        "⛰️" -> Icons.Default.Terrain
        "🏖️" -> Icons.Default.BeachAccess
        "🛍️" -> Icons.Default.ShoppingBag
        "🎭" -> Icons.Default.TheaterComedy // Or other relevant icon
        "🎶" -> Icons.Default.MusicNote
        "🚗" -> Icons.Default.DirectionsCar
        "🚶" -> Icons.Default.DirectionsWalk
        "🌅" -> Icons.Default.WbSunny // Or a custom sunrise/sunset icon
        "🚕" -> Icons.Default.LocalTaxi
        "⛵" -> Icons.Default.Sailing
        "🪂" -> Icons.Default.Paragliding
        // Add more mappings as needed
        else -> Icons.Default.Place // Default icon
    }
}


// TimelineNode Composable (Ensure it takes DisplayableTimelineEvent or adapt it)
@Composable
fun TimelineNode(
    event: DisplayableTimelineEvent, // Or your original TimelineEvent if you haven't changed it
    isLastNode: Boolean,
    orangeColor: Color,
    onClick: () -> Unit // <--- ADD THIS PARAMETER
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick) // <--- APPLY THE CLICKABLE MODIFIER HERE
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (event.isPast) Color.Transparent else orangeColor)
                    .border(2.dp, if (event.isPast) Color.Gray else orangeColor, CircleShape)
            )
            if (!isLastNode) {
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLastNode) 0.dp else 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(event.time, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(event.subtitle, color = Color.Gray, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(event.icon, contentDescription = event.title, tint = Color.Gray)
            }
        }
    }
}


// --- Previews ---

@Preview(showBackground = true, name = "Itinerary Form Screen - Empty", widthDp = 360, heightDp = 800)
@Composable
fun ItineraryFormScreenEmptyPreview() {
    MaterialTheme {
        // For preview, you can pass a ViewModel that returns empty lists
        // or mock the ViewModelProvider to return a specific instance.
        // This is a simplified preview:
        ItineraryFormScreen(
            onNavigateToAddItinerary = { /* Preview doesn't need real navigation */ },
            itineraryViewModel = viewModel(
                factory = ItineraryViewModelFactory(
                    // Mock repository for preview that returns empty data
                    ItineraryRepository(MockItineraryDayDaoEmpty(), MockItineraryActivityDaoEmpty())
                )
            )
        )
    }
}

@Preview(showBackground = true, name = "Itinerary Form Screen - With Data", widthDp = 360, heightDp = 800)
@Composable
fun ItineraryFormScreenWithDataPreview() {
    MaterialTheme {
        ItineraryFormScreen(
            onNavigateToAddItinerary = { /* Preview doesn't need real navigation */ },
            itineraryViewModel = viewModel(
                factory = ItineraryViewModelFactory(
                    // Mock repository for preview that returns empty data
                    ItineraryRepository(MockItineraryDayDaoEmpty(), MockItineraryActivityDaoEmpty())
                )
            )
        )
    }
}


class MockItineraryDayDaoEmpty : ItineraryDayDao {
    override suspend fun insertDay(day: ItineraryDay): Long = 1L
    override suspend fun updateDay(day: ItineraryDay) {}
    override suspend fun deleteDay(day: ItineraryDay) {}
    override fun getAllDays(): Flow<List<ItineraryDay>> = flowOf(emptyList())
    override fun getDayById(dayId: Long): Flow<ItineraryDay?> = flowOf(null)
}
class MockItineraryActivityDaoEmpty : ItineraryActivityDao {
    override suspend fun insertActivity(activity: ItineraryActivity) {}
    override suspend fun updateActivity(activity: ItineraryActivity) {}
    override suspend fun deleteActivity(activity: ItineraryActivity) {}
    override fun getActivitiesForDay(dayId: Long): Flow<List<ItineraryActivity>> = flowOf(emptyList())
    override fun getActivityById(activityId: Long): Flow<ItineraryActivity?> = flowOf(null)
}

class MockItineraryDayDaoWithData :ItineraryDayDao {
    private val sampleDays = listOf(
        ItineraryDay(1, "Day 1", "July 14"),
        ItineraryDay(2, "Day 2", "July 15")
    )
    override suspend fun insertDay(day: ItineraryDay): Long = 3L
    override suspend fun updateDay(day: ItineraryDay) {}
    override suspend fun deleteDay(day: ItineraryDay) {}
    override fun getAllDays(): Flow<List<ItineraryDay>> = flowOf(sampleDays)
    override fun getDayById(dayId: Long): Flow<ItineraryDay?> = flowOf(sampleDays.find { it.id == dayId })
}
class MockItineraryActivityDaoWithData : ItineraryActivityDao {
    private val sampleActivities = listOf(
        ItineraryActivity(1, 1, "09:00", "Breakfast", "🍽️"),
        ItineraryActivity(2, 1, "10:30", "Beach Time", "🏖️"),
        ItineraryActivity(3, 2, "14:00", "Museum Visit", "🗺️")
    )
    override suspend fun insertActivity(activity: ItineraryActivity) {}
    override suspend fun updateActivity(activity: ItineraryActivity) {}
    override suspend fun deleteActivity(activity: ItineraryActivity) {}
    override fun getActivitiesForDay(dayId: Long): Flow<List<ItineraryActivity>> =
        flowOf(sampleActivities.filter { it.dayId == dayId })
    override fun getActivityById(activityId: Long): Flow<ItineraryActivity?> =
        flowOf(sampleActivities.find { it.id == activityId })
}
