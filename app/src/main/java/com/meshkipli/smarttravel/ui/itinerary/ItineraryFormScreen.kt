package com.meshkipli.smarttravel.ui.itinerary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.meshkipli.smarttravel.ui.common.DisplayableTimelineEvent
import com.meshkipli.smarttravel.ui.itinerary.components.TimelineNode
import com.meshkipli.smarttravel.ui.itinerary.components.emojiToIcon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryFormScreen(
    onNavigateToAddItinerary: () -> Unit,
    itineraryViewModel: ItineraryViewModel = viewModel(
        factory = ItineraryViewModelFactory(
            (LocalContext.current.applicationContext as SmartTravelApplication).itineraryRepository
        )
    )
) {
    val orangeColor = Color(0xFFF9882B)
    val days by itineraryViewModel.allDays.collectAsState()
    val activities by itineraryViewModel.activitiesForSelectedDay.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val categories = listOf("Island", "Beach", "Resort")
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }

    LaunchedEffect(days, selectedTabIndex) {
        if (days.isNotEmpty() && selectedTabIndex < days.size) {
            itineraryViewModel.selectDay(days[selectedTabIndex].id)
        } else {
            itineraryViewModel.selectDay(null)
        }
    }


    val timelineEvents = remember(activities) {
        activities.map { activity ->
            DisplayableTimelineEvent(
                time = activity.time,
                title = activity.name,
                subtitle = "Activity Details",
                icon = emojiToIcon(activity.emoji),
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
        floatingActionButton = { 
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
                        .weight(1f) 
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
                
                TabRow(
                    selectedTabIndex = selectedTabIndex.coerceIn(0, days.size -1), 
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
                                    "${day.dayLabel}\n${day.date}", 
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
                                 onClick = { event.originalActivity?.let { activity ->  } }
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true, name = "Itinerary Form Screen - Empty", widthDp = 360, heightDp = 800)
@Composable
fun ItineraryFormScreenEmptyPreview() {
    MaterialTheme {
        ItineraryFormScreen(
            onNavigateToAddItinerary = { },
            itineraryViewModel = viewModel(
                factory = ItineraryViewModelFactory(
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
            onNavigateToAddItinerary = {  },
            itineraryViewModel = viewModel(
                factory = ItineraryViewModelFactory(
                    
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

