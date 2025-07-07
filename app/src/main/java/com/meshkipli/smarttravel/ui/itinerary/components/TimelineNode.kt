package com.meshkipli.smarttravel.ui.itinerary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.meshkipli.smarttravel.ui.common.DisplayableTimelineEvent

@Composable
fun TimelineNode(
    event: DisplayableTimelineEvent,
    isLastNode: Boolean,
    orangeColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick)
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