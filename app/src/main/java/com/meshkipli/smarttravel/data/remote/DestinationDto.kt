package com.meshkipli.smarttravel.data.remote// Create these in a suitable package, e.g., com.meshkipli.smarttravel.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DestinationDto(
    val id: String,
    val name: String,
    val description: String? = null, // Making it nullable as it might not always be crucial for display
    val region: String? = null,
    val country: String? = null,
    @SerialName("coverImage")
    val coverImage: String?,
    // val user: UserDto? = null, // You can include this if needed
    // val createdAt: String? = null,
    // val updatedAt: String? = null
)


@Serializable
data class DestinationsApiResponse(
    val data: List<DestinationDto>
)


