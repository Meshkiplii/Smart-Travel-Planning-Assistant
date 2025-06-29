package com.meshkipli.smarttravel.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.R


// --- Data Models ---
data class DateRange(val start: Int, val end: Int)


// --- Screen Components ---

@Composable
fun LocationSummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.img_nordic_cottage),
                contentDescription = "Nordic Cottage",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent),
                            endX = 500f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Nordic Cottage", color = Color.White.copy(alpha = 0.8f))
                Text(
                    "Nordic Cottage",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
fun CalendarHeader(month: String, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Select dates", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onPrev) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
        }
        Text(month, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
        }
    }
}

@Composable
fun CalendarView(selectedRange: DateRange) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    // Static data for March 2023 for preview purposes
    val weeks = listOf(
        listOf(null, null, 1, 2, 3, 4, 5),
        listOf(6, 7, 8, 9, 10, 11, 12),
        listOf(13, 14, 15, 16, 17, 18, 19),
        listOf(20, 21, 22, 23, 24, 25, 26),
        listOf(27, 28, 29, 30, null, null, null)
    )
    val orangeColor = Color(0xFFF9882B)
    val lightOrangeColor = orangeColor.copy(alpha = 0.2f)

    Column {
        // Days of the week header
        Row(Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Dates grid
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    val isSelected = day != null && day >= selectedRange.start && day <= selectedRange.end
                    val isStart = day == selectedRange.start
                    val isEnd = day == selectedRange.end

                    // Determine the background shape for the range
                    val rangeShape = when {
                        isStart && isEnd -> CircleShape
                        isStart -> RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50)
                        isEnd -> RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
                        isSelected -> RoundedCornerShape(0.dp)
                        else -> RoundedCornerShape(0.dp)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f) // Makes the cell a square
                            .background(
                                if (isSelected) lightOrangeColor else Color.Transparent,
                                shape = rangeShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            val isStartOrEnd = isStart || isEnd
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(0.8f)
                                    .clip(CircleShape)
                                    .background(if (isStartOrEnd) orangeColor else Color.Transparent)
                                    .clickable { /* Handle date click */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    color = when {
                                        isStartOrEnd -> Color.White
                                        else -> Color.Black
                                    },
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}


// --- Main Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTripScreen() {
    var journeyQuery by remember { mutableStateOf("") }
    var sendToEmail by remember { mutableStateOf(true) }
    val orangeColor = Color(0xFFF9882B)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Plan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            LocationSummaryCard()
            Spacer(modifier = Modifier.height(24.dp))
            CalendarHeader(month = "March", onPrev = {}, onNext = {})
            Spacer(modifier = Modifier.height(8.dp))
            CalendarView(selectedRange = DateRange(start = 21, end = 24))
            Spacer(modifier = Modifier.height(24.dp))

            // This title is redundant but matches the design
            Text("Select dates", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = journeyQuery,
                    onValueChange = { journeyQuery = it },
                    placeholder = { Text("Query journey", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE0E0E0),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = { /* Handle send */ },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(orangeColor)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Send to your email", modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Switch(
                    checked = sendToEmail,
                    onCheckedChange = { sendToEmail = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = orangeColor,
                        checkedTrackColor = orangeColor.copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { /* Handle next step */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeColor)
            ) {
                Text("Next step", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


// --- Preview ---

@Preview(showBackground = true, name = "Schedule Trip Screen", widthDp = 360, heightDp = 800)
@Composable
fun ScheduleTripScreenPreview() {
    MaterialTheme {
        ScheduleTripScreen()
    }
}