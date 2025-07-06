package com.meshkipli.smarttravel.ui.common

import androidx.compose.ui.graphics.vector.ImageVector
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryActivity

// Data class for Timeline display (can be adapted or replaced)
data class DisplayableTimelineEvent(
    val time: String,
    val title: String,
    val subtitle: String, // Could be derived from activity details
    val icon: ImageVector,
    val isPast: Boolean = false, // You might need logic to determine this
    val originalActivity: ItineraryActivity? = null // To link back if needed
)