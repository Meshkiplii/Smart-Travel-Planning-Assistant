package com.meshkipli.smarttravel.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Data Models ---

data class ItineraryActivity(
    val time: String,
    val description: String,
    val emoji: String? = null
)

data class ItineraryDay(
    val dayLabel: String,
    val date: String,
    val activities: List<ItineraryActivity>
)

data class TimelineEvent(
    val time: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val isPast: Boolean = false
)


// --- Screen 1: Add Itinerary ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItineraryScreen() {
    val orangeColor = Color(0xFFF9882B)
    val lightOrangeColor = Color(0xFFFDECDD)
    var selectedTabIndex by remember { mutableStateOf(0) }

    val days = listOf(
        ItineraryDay("Day 1", "July 14", listOf(
            ItineraryActivity("5:30", "Wake up", "🌅"),
            ItineraryActivity("7:30", "City tour", "🚕"),
            ItineraryActivity("8:30", "Sailing project", "⛵"),
            ItineraryActivity("9:30", "Skydiving", "🪂")
        )),
        ItineraryDay("Day 2", "July 15", listOf()),
        ItineraryDay("Day 3", "July 16", listOf())
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add itinerary", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // Day Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 3.dp,
                        color = orangeColor
                    )
                }
            ) {
                days.forEachIndexed { index, day ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Color.Gray,
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(day.dayLabel, fontWeight = FontWeight.Bold)
                            Text(day.date, fontSize = 12.sp)
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
                days[selectedTabIndex].activities.forEach { activity ->
                    ActivityRow(activity)
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Add Activity Button
                Button(
                    onClick = { /* Handle add activity */ },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lightOrangeColor,
                        contentColor = orangeColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Add activity", fontWeight = FontWeight.Bold)
                }
            }

            // Bottom Button
            Button(
                onClick = { /* Handle next step */ },
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
}

@Composable
fun ActivityRow(activity: ItineraryActivity) {
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
                Text(activity.description, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                if (activity.emoji != null) {
                    Text(activity.emoji, fontSize = 20.sp)
                }
            }
        }
    }
}


// --- Screen 2: Itinerary Form ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryFormScreen() {
    val orangeColor = Color(0xFFF9882B)
    var selectedTabIndex by remember { mutableStateOf(1) }
    val categories = listOf("Island", "Beach", "Resort")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    val timelineEvents = listOf(
        TimelineEvent("12:30", "Maldives", "Save the Turtles", Icons.Default.WbCloudy),
        TimelineEvent("14:30", "Golden beach", "Surfing on the sea", Icons.Default.Thunderstorm, isPast = true),
        TimelineEvent("17:30", "Coconut grove", "BBQ party by the sea", Icons.Default.WbCloudy, isPast = true),
        TimelineEvent("21:30", "Maldives Islands", "Sea blowing", Icons.Default.Air, isPast = true)
    )

    val navItems = listOf(
        BottomNavItem("Home", Icons.Outlined.Home, false),
        BottomNavItem("Wallet", Icons.Outlined.AccountBalanceWallet, false),
        BottomNavItem("Guide", Icons.Filled.Explore, true),
        BottomNavItem("Chart", Icons.Outlined.BarChart, false)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itinerary Form", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle action */ }) {
                        Icon(Icons.Outlined.CropFree, contentDescription = "Scan")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
//        bottomBar = { AppBottomNavigation(items = navItems, orangeColor = orangeColor) },
        containerColor = Color(0xFFF9F9F9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Category Chips
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
                            borderWidth = 1.dp, // Assuming a 1.dp border for consistency
                            borderColor = Color.LightGray.copy(alpha = 0.5f), // Color when not selected & enabled
                            selectedBorderColor = orangeColor,         // Color when selected & enabled
                            // disabledBorderColor = ...,             // Optionally specify for disabled states
                            // disabledSelectedBorderColor = ...,       // Optionally specify for disabled & selected states
                            enabled = true,                            // Chip's enabled state
                            selected = isSelected                      // Chip's selected state
                        )
                    )
                }
            }

            // Day Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 3.dp,
                        color = orangeColor
                    )
                }
            ) {
                listOf("Day 1\nJuly 14", "Day 2\nJuly 15", "Day 3\nJuly 16").forEachIndexed { index, text ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text,
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

            // Timeline
            Column(modifier = Modifier.padding(24.dp)) {
                timelineEvents.forEachIndexed { index, event ->
                    TimelineNode(
                        event = event,
                        isLastNode = index == timelineEvents.lastIndex,
                        orangeColor = orangeColor
                    )
                }
            }

            // Bottom Button
            Button(
                onClick = { /* Handle view itinerary */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View specific itinerary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun TimelineNode(event: TimelineEvent, isLastNode: Boolean, orangeColor: Color) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
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

// --- Shared Components for Preview ---

data class BottomNavItem(val label: String, val icon: ImageVector, val isSelected: Boolean)


// --- Previews ---

@Preview(showBackground = true, name = "Add Itinerary Screen", widthDp = 360, heightDp = 800)
@Composable
fun AddItineraryScreenPreview() {
    MaterialTheme {
        AddItineraryScreen()
    }
}

@Preview(showBackground = true, name = "Itinerary Form Screen", widthDp = 360, heightDp = 800)
@Composable
fun ItineraryFormScreenPreview() {
    MaterialTheme {
        ItineraryFormScreen()
    }
}