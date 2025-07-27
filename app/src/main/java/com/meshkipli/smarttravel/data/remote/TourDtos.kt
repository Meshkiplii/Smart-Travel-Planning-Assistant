package com.meshkipli.smarttravel.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ToursApiResponse(
    val data: List<TourDto>,
    val meta: MetaDto
)

@Serializable
data class TourDto(
    val id: String,
    val title: String,
    val categoryId: String? = null,
    val description: String,
    val about: String, // This is HTML content
    val startDate: String, // Consider using kotlinx-datetime for proper date handling
    val endDate: String,   // Consider using kotlinx-datetime
    val pricePerPerson: Double, // API shows Int, your mock used Int
    val currency: String,
    val isFeatured: Boolean,
    val coverImage: String, // This is a URL
    val destination: TourDestinationDto,
    val user: TourUserDto, // Renamed from UserDto to avoid conflict if you have another
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class TourDestinationDto(
    val id: String,
    val name: String
)

@Serializable
data class TourUserDto( // Assuming this is different from your auth UserDto
    val id: String,
    val name: String,
    val username: String,
    val profileImage: String?, // Nullable
    val role: String
)

@Serializable
data class MetaDto(
    val limit: Int,
    val page: Int,
    val total: Int,
    val total_pages: Int
)

