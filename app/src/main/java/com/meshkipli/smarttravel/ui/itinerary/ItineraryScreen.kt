package com.meshkipli.smarttravel.ui.itinerary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meshkipli.smarttravel.SmartTravelApplication
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryActivity
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryDay
import com.meshkipli.smarttravel.ui.itinerary.components.AddEditItineraryActivityDialog
import com.meshkipli.smarttravel.ui.itinerary.components.AddEditItineraryDayDialog


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
    Color(0xFFFDECDD)

    
    val days by itineraryViewModel.allDays.collectAsState()
    
    val activities by itineraryViewModel.activitiesForSelectedDay.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    
    LaunchedEffect(days, selectedTabIndex) {
        if (days.isNotEmpty() && selectedTabIndex < days.size) {
            itineraryViewModel.selectDay(days[selectedTabIndex].id)
        } else {
            itineraryViewModel.selectDay(0L) 
        }
    }

    
    var showAddDayDialog by remember { mutableStateOf(false) }
    var showAddActivityDialog by remember { mutableStateOf(false) }
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
        containerColor = Color.White
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
                
                TabRow(
                    selectedTabIndex = selectedTabIndex.coerceIn(0, days.size -1), 
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

                            }
                        }
                    }
                }

                
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
                    
                    Button(
                        onClick = {
                            if (days.isNotEmpty() && selectedTabIndex < days.size) {
                                showAddActivityDialog = true 
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = orangeColor,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = days.isNotEmpty() 
                    ) {
                        Text("Add activity", fontWeight = FontWeight.Bold)
                    }
                }
            }

        }
    }

    if (showAddDayDialog) {
        AddEditItineraryDayDialog(
            dayToEdit = null, 
            onDismiss = { showAddDayDialog = false },
            onSave = { dayLabel, date ->
                itineraryViewModel.addItineraryDay(dayLabel, date)
                showAddDayDialog = false
            }
        )
    }

    

    val currentSelectedDayId = if (days.isNotEmpty() && selectedTabIndex < days.size) days[selectedTabIndex].id else null
    if (showAddActivityDialog && currentSelectedDayId != null) {
        AddEditItineraryActivityDialog(
            activityToEdit = null, 
            dayId = currentSelectedDayId,
            onDismiss = { showAddActivityDialog = false },
            onSave = { _, time, name, emoji -> 
                itineraryViewModel.addItineraryActivity(currentSelectedDayId, time, name, emoji)
                showAddActivityDialog = false
            }
        )
    }

    if (activityToEdit != null) {
        AddEditItineraryActivityDialog(
            activityToEdit = activityToEdit,
            dayId = activityToEdit!!.dayId, 
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
    activity: ItineraryActivity, 
    onEdit: (ItineraryActivity) -> Unit,
    onDelete: (ItineraryActivity) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        
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
        Box { 
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    Icons.Default.MoreVert, 
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
                        showMenu = false 
                    },
                    leadingIcon = { 
                        Icon(Icons.Default.Edit, contentDescription = "Edit Activity")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        onDelete(activity)
                        showMenu = false 
                    },
                    leadingIcon = { 
                        Icon(Icons.Default.Delete, contentDescription = "Delete Activity")
                    }
                )
            }
        }
    }
    }



@Preview(showBackground = true, name = "Add Itinerary Screen", widthDp = 360, heightDp = 800)
@Composable
fun AddItineraryScreenPreview() {
    MaterialTheme {
        AddItineraryScreen(onNavigateBack = {})
    }
}
