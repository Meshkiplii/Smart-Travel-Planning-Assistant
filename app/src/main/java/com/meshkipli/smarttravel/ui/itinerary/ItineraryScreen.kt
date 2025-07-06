package com.meshkipli.smarttravel.ui.itinerary

import android.app.TimePickerDialog
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.TimePicker
import android.widget.DatePicker
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
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.format

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
                onClick = { onNavigateBack()},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
    var showMenu by remember { mutableStateOf(false) }
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
        Box { // Box is used to anchor the DropdownMenu
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    Icons.Default.MoreVert, // Vertical three-dots icon
                    contentDescription = "More options"
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        onEdit(activity)
                        showMenu = false // Dismiss menu after action
                    },
                    leadingIcon = { // Optional: add an icon to the menu item
                        Icon(Icons.Default.Edit, contentDescription = "Edit Activity")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        onDelete(activity)
                        showMenu = false // Dismiss menu after action
                    },
                    leadingIcon = { // Optional: add an icon to the menu item
                        Icon(Icons.Default.Delete, contentDescription = "Delete Activity")
                    }
                )
            }
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

    // --- Date Picker Integration ---
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Initial date state: use dayToEdit's date if available, else current date
    val initialDateString = dayToEdit?.date
    var selectedDate by remember {
        mutableStateOf(
            if (!initialDateString.isNullOrBlank()) {
                try {
                    // Attempt to parse the existing date string
                    SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).parse(initialDateString)?.time ?: calendar.timeInMillis
                } catch (e: Exception) {
                    calendar.timeInMillis // Fallback to current date if parsing fails
                }
            } else {
                calendar.timeInMillis // Default to current date for new day
            }
        )
    }

    // Formatter for displaying the date in the TextField
    val dateFormatter = remember { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) }
    var formattedDate by remember(selectedDate) {
        mutableStateOf(dateFormatter.format(selectedDate))
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val newCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                selectedDate = newCalendar.timeInMillis // Update the selectedDate state (as Long)
                // formattedDate will update automatically via remember(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // Disable days before the current day
            datePicker.minDate = Calendar.getInstance().timeInMillis // Sets minDate to today
        }
    }
    // --- End of Date Picker Integration ---

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (dayToEdit == null) "Add New Day" else "Edit Day") },
        text = {
            Column {
                OutlinedTextField(
                    value = dayLabel,
                    onValueChange = { dayLabel = it },
                    label = { Text("Day Label (e.g., Day 1)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Date TextField - Clickable to show DatePickerDialog
                OutlinedTextField(
                    value = formattedDate, // Display the formatted date
                    onValueChange = { /* Not directly editable */ },
                    label = { Text("Date") },
                    readOnly = true, // Make it not directly editable
                    trailingIcon = {
                        Icon(Icons.Filled.CalendarToday, "Select Date")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() } // Show picker on click
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (dayLabel.isNotBlank() && formattedDate.isNotBlank()) {
                    onSave(dayLabel, formattedDate) // Save the formatted date string
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
    dayId: Long,
    onDismiss: () -> Unit,
    onSave: (activity: ItineraryActivity, time: String, name: String, emoji: String?) -> Unit
) {
    // --- Time Picker Integration ---
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Initial time state: use activityToEdit's time if available, else current time
    val initialTimeString = activityToEdit?.time
    var selectedHour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }

    LaunchedEffect(initialTimeString) {
        if (!initialTimeString.isNullOrBlank()) {
            try {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // Assuming "HH:mm" format
                val date = timeFormat.parse(initialTimeString)
                if (date != null) {
                    val cal = Calendar.getInstance().apply { time = date }
                    selectedHour = cal.get(Calendar.HOUR_OF_DAY)
                    selectedMinute = cal.get(Calendar.MINUTE)
                }
            } catch (e: Exception) {
                // Could not parse, keep current time as default
                println("Error parsing initial time: $e")
            }
        }
    }


    // Formatter for displaying the time
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) } // e.g., "09:00"
    var formattedTime by remember(selectedHour, selectedMinute) {
        mutableStateOf(
            timeFormatter.format(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }.time
            )
        )
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _: TimePicker, hour: Int, minute: Int ->
                selectedHour = hour
                selectedMinute = minute
                // formattedTime will update automatically
            },
            selectedHour,
            selectedMinute,
            true // true for 24-hour format, false for AM/PM
        )
    }
    // --- End of Time Picker Integration ---

    var name by remember { mutableStateOf(activityToEdit?.name ?: "") }
    var emoji by remember { mutableStateOf(activityToEdit?.emoji ?: "") }
    val emojis = listOf("🎉", "✈️", "🏨", "🍽️", "🗺️", "⛰️", "🏖️", "🛍️", "🎭", "🎶", "🚗", "🚶", "🌅", "🚕", "⛵", "🪂")
    var showEmojiPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (activityToEdit == null) "Add New Activity" else "Edit Activity") },
        text = {
            Column {
                // Time TextField - Clickable to show TimePickerDialog
                OutlinedTextField(
                    value = formattedTime, // Display formatted time
                    onValueChange = { /* Not directly editable */ },
                    label = { Text("Time") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Outlined.Schedule, "Select Time")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { timePickerDialog.show() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Activity Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = emoji,
                        onValueChange = { emoji = it },
                        label = { Text("Emoji (Optional)") },
                        modifier = Modifier.weight(1f),
                        readOnly = true
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
                if (formattedTime.isNotBlank() && name.isNotBlank()) {
                    val currentActivity = activityToEdit ?: ItineraryActivity(dayId = dayId, time = "", name = "")
                    onSave(currentActivity, formattedTime, name, emoji.ifBlank { null })
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
