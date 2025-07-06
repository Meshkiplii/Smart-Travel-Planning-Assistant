package com.meshkipli.smarttravel.ui.itinerary

import android.content.Intent
import androidx.compose.animation.core.copy
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryActivity
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryDay

// --- Screen 1: Add Itinerary ---


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItineraryScreen(
    onNavigateBack: () -> Unit,
    itineraryViewModel: ItineraryViewModel = viewModel(
        factory = ItineraryViewModelFactory(
            (LocalContext.current.applicationContext as SmartTravelApplication).itineraryRepository
        )
    )
) {
    val orangeColor = Color(0xFFF9882B)
    val lightOrangeColor = Color(0xFFFDECDD)

    // Observe days from the ViewModel
    val days by itineraryViewModel.allDays.collectAsState()
    // Observe activities for the selected day
    val activities by itineraryViewModel.activitiesForSelectedDay.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Update selectedDayId in ViewModel when tab changes and days list is not empty
    LaunchedEffect(days, selectedTabIndex) {
        if (days.isNotEmpty() && selectedTabIndex < days.size) {
            itineraryViewModel.selectDay(days[selectedTabIndex].id)
        } else {
            itineraryViewModel.selectDay(0L) // Or handle empty state appropriately
        }
    }

    // Dialog states for adding/editing days and activities
    var showAddDayDialog by remember { mutableStateOf(false) }
    var showAddActivityDialog by remember { mutableStateOf(false) }
    var dayToEdit by remember { mutableStateOf<ItineraryDay?>(null) }
    var activityToEdit by remember { mutableStateOf<ItineraryActivity?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Itinerary", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDayDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Day")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (days.isEmpty()) {
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No itinerary days yet. Add one to get started!")
                }
            } else {
                // Day Tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex.coerceIn(0, days.size -1), // Ensure index is valid
                    containerColor = Color.White,
                    indicator = { tabPositions ->
                        if (tabPositions.isNotEmpty() && selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.coerceIn(0, tabPositions.size -1)]),
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
                            selectedContentColor = Color.Black,
                            unselectedContentColor = Color.Gray,
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(day.dayLabel, fontWeight = FontWeight.Bold)
                                    Text(day.date, fontSize = 12.sp)
                                }
                                // IconButton(onClick = { /* TODO: Edit Day */ dayToEdit = day }) {
                                //     Icon(Icons.Default.Edit, contentDescription = "Edit Day", Modifier.size(18.dp))
                                // }
                                // IconButton(onClick = { itineraryViewModel.deleteItineraryDay(day) }) {
                                //     Icon(Icons.Default.Delete, contentDescription = "Delete Day", Modifier.size(18.dp))
                                // }
                            }
                        }
                    }
                }

                // Activities List
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    activities.forEach { activity ->
                        ActivityRow(
                            activity = activity,
                            onEdit = { activityToEdit = activity },
                            onDelete = { itineraryViewModel.deleteItineraryActivity(activity) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Add Activity Button
                    Button(
                        onClick = {
                            if (days.isNotEmpty() && selectedTabIndex < days.size) {
                                showAddActivityDialog = true // Open dialog to add activity
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = lightOrangeColor,
                            contentColor = orangeColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = days.isNotEmpty() // Only enable if there's a selected day
                    ) {
                        Text("Add activity", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom Button (Example - can be removed or repurposed)
            Button(
                onClick = { /* Handle next step or other action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Next step", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    if (showAddDayDialog) {
        AddEditItineraryDayDialog(
            dayToEdit = null, // Passing null for adding new day
            onDismiss = { showAddDayDialog = false },
            onSave = { dayLabel, date ->
                itineraryViewModel.addItineraryDay(dayLabel, date)
                showAddDayDialog = false
            }
        )
    }

    // TODO: Implement Edit Day Dialog (when dayToEdit is not null)

    val currentSelectedDayId = if (days.isNotEmpty() && selectedTabIndex < days.size) days[selectedTabIndex].id else null
    if (showAddActivityDialog && currentSelectedDayId != null) {
        AddEditItineraryActivityDialog(
            activityToEdit = null, // For adding new
            dayId = currentSelectedDayId,
            onDismiss = { showAddActivityDialog = false },
            onSave = { _, time, name, emoji -> // dayId is handled internally or passed
                itineraryViewModel.addItineraryActivity(currentSelectedDayId, time, name, emoji)
                showAddActivityDialog = false
            }
        )
    }

    if (activityToEdit != null) {
        AddEditItineraryActivityDialog(
            activityToEdit = activityToEdit,
            dayId = activityToEdit!!.dayId, // Activity must have a dayId
            onDismiss = { activityToEdit = null },
            onSave = { updatedActivity, time, name, emoji ->
                itineraryViewModel.updateItineraryActivity(
                    updatedActivity.copy(
                        time = time,
                        name = name,
                        emoji = emoji
                    )
                )
                activityToEdit = null
            }
        )
    }
}
@Composable
fun ActivityRow(
    activity: ItineraryActivity, // Use your Room entity
    onEdit: (ItineraryActivity) -> Unit,
    onDelete: (ItineraryActivity) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Time Chip
        Surface(
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Schedule,
                    contentDescription = "Time",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(activity.time, fontWeight = FontWeight.SemiBold)
            }
        }

        // Description Chip
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(activity.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                if (activity.emoji != null) {
                    Text(activity.emoji, fontSize = 20.sp)
                }
            }
        }
        IconButton(onClick = { onEdit(activity) }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Activity")
        }
        IconButton(onClick = { onDelete(activity) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Activity")
        }
    }
}


// --- Dialog for Adding/Editing Itinerary Day ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItineraryDayDialog(
    dayToEdit: ItineraryDay?,
    onDismiss: () -> Unit,
    onSave: (dayLabel: String, date: String) -> Unit
) {
    var dayLabel by remember { mutableStateOf(dayToEdit?.dayLabel ?: "") }
    var date by remember { mutableStateOf(dayToEdit?.date ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (dayToEdit == null) "Add New Day" else "Edit Day") },
        text = {
            Column {
                OutlinedTextField(
                    value = dayLabel,
                    onValueChange = { dayLabel = it },
                    label = { Text("Day Label (e.g., Day 1)") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (e.g., July 14)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (dayLabel.isNotBlank() && date.isNotBlank()) {
                    onSave(dayLabel, date)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// --- Dialog for Adding/Editing Itinerary Activity ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItineraryActivityDialog(
    activityToEdit: ItineraryActivity?,
    dayId: Long, // Needed to associate with a day
    onDismiss: () -> Unit,
    onSave: (activity: ItineraryActivity, time: String, name: String, emoji: String?) -> Unit
) {
    var time by remember { mutableStateOf(activityToEdit?.time ?: "") }
    var name by remember { mutableStateOf(activityToEdit?.name ?: "") }
    var emoji by remember { mutableStateOf(activityToEdit?.emoji ?: "") } // Default emoji or empty

    // Simple Emoji Picker (Replace with a more robust one if needed)
    val emojis = listOf("🎉", "✈️", "🏨", "🍽️", "🗺️", "⛰️", "🏖️", "🛍️", "🎭", "🎶", "🚗", "🚶", "🌅", "🚕", "⛵", "🪂")
    var showEmojiPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (activityToEdit == null) "Add New Activity" else "Edit Activity") },
        text = {
            Column {
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (e.g., 09:00)") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Activity Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = emoji,
                        onValueChange = { emoji = it },
                        label = { Text("Emoji (Optional)") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showEmojiPicker = true }) {
                        Icon(Icons.Outlined.SentimentSatisfiedAlt, "Pick Emoji")
                    }
                }

                if (showEmojiPicker) {
                    AlertDialog(
                        onDismissRequest = { showEmojiPicker = false },
                        title = { Text("Select Emoji") },
                        text = {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(count = emojis.size) { index ->
                                    val selected = emojis[index]
                                    TextButton(onClick = {
                                        emoji = selected
                                        showEmojiPicker = false
                                    }) {
                                        Text(selected, fontSize = 24.sp)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showEmojiPicker = false }) { Text("Close") }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (time.isNotBlank() && name.isNotBlank()) {
                    val currentActivity = activityToEdit ?: ItineraryActivity(dayId = dayId, time = "", name = "") // Create a dummy if new
                    onSave(currentActivity, time, name, emoji.ifBlank { null })
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// --- Previews ---

@Preview(showBackground = true, name = "Add Itinerary Screen", widthDp = 360, heightDp = 800)
@Composable
fun AddItineraryScreenPreview() {
    MaterialTheme {
        AddItineraryScreen(onNavigateBack = {})
    }
}
