package com.meshkipli.smarttravel.ui.itinerary.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryActivity
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryDay
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItineraryDayDialog(
    dayToEdit: ItineraryDay?,
    onDismiss: () -> Unit,
    onSave: (dayLabel: String, date: String) -> Unit
) {
    var dayLabel by remember { mutableStateOf(dayToEdit?.dayLabel ?: "") }

    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    
    val initialDateString = dayToEdit?.date
    var selectedDate by remember {
        mutableStateOf(
            if (!initialDateString.isNullOrBlank()) {
                try {
                    SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).parse(initialDateString)?.time ?: calendar.timeInMillis
                } catch (e: Exception) {
                    calendar.timeInMillis 
                }
            } else {
                calendar.timeInMillis 
            }
        )
    }

    
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
                selectedDate = newCalendar.timeInMillis 
                
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            
            datePicker.minDate = Calendar.getInstance().timeInMillis 
        }
    }
    

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

                
                OutlinedTextField(
                    value = formattedDate, 
                    onValueChange = {  },
                    label = { Text("Date") },
                    readOnly = true, 
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            datePickerDialog.show()
                        }) {
                            Icon(Icons.Filled.CalendarToday, "Select Date")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (dayLabel.isNotBlank() && formattedDate.isNotBlank()) {
                    onSave(dayLabel, formattedDate) 
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItineraryActivityDialog(
    activityToEdit: ItineraryActivity?,
    dayId: Long,
    onDismiss: () -> Unit,
    onSave: (activity: ItineraryActivity, time: String, name: String, emoji: String?) -> Unit
) {
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    
    val initialTimeString = activityToEdit?.time
    var selectedHour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }

    LaunchedEffect(initialTimeString) {
        if (!initialTimeString.isNullOrBlank()) {
            try {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) 
                val date = timeFormat.parse(initialTimeString)
                if (date != null) {
                    val cal = Calendar.getInstance().apply { time = date }
                    selectedHour = cal.get(Calendar.HOUR_OF_DAY)
                    selectedMinute = cal.get(Calendar.MINUTE)
                }
            } catch (e: Exception) {
                
                println("Error parsing initial time: $e")
            }
        }
    }


    
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) } 
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
                
            },
            selectedHour,
            selectedMinute,
            true 
        )
    }
    

    var name by remember { mutableStateOf(activityToEdit?.name ?: "") }
    var emoji by remember { mutableStateOf(activityToEdit?.emoji ?: "") }
    val emojis = listOf("🎉", "✈️", "🏨", "🍽️", "🗺️", "⛰️", "🏖️", "🛍️", "🎭", "🎶", "🚗", "🚶", "🌅", "🚕", "⛵", "🪂")
    var showEmojiPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (activityToEdit == null) "Add New Activity" else "Edit Activity") },
        text = {
            Column {
                
                OutlinedTextField(
                    value = formattedTime, 
                    onValueChange = {  },
                    label = { Text("Time") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            timePickerDialog.show()
                        }) {
                            Icon(Icons.Outlined.Schedule, "Select Time")
                        }
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