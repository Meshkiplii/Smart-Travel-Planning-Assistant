package com.meshkipli.smarttravel.ui.itinerary.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

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
        "🎭" -> Icons.Default.TheaterComedy
        "🎶" -> Icons.Default.MusicNote
        "🚗" -> Icons.Default.DirectionsCar
        "🚶" -> Icons.Default.DirectionsWalk
        "🌅" -> Icons.Default.WbSunny
        "🚕" -> Icons.Default.LocalTaxi
        "⛵" -> Icons.Default.Sailing
        "🪂" -> Icons.Default.Paragliding
        else -> Icons.Default.Place
    }
}