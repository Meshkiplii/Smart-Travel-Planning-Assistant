package com.meshkipli.smarttravel.data.local.db.entities // It's good practice to put data classes in a separate package

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "itinerary_days")
data class ItineraryDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayLabel: String, // e.g., "Day 1"
    val date: String // e.g., "July 14"
)

@Entity(
    tableName = "itinerary_activities",
    foreignKeys = [
        ForeignKey(
            entity = ItineraryDay::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE // If a day is deleted, its activities are also deleted
        )
    ]
)
data class ItineraryActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayId: Long, // Foreign key to link to ItineraryDay
    val time: String,
    val name: String, // Changed from 'description' to 'name' as per your request
    val emoji: String? = null
)
