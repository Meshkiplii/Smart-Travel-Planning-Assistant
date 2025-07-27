package com.meshkipli.smarttravel.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String
)

@Serializable
data class RegisterResponse(
    val token: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val username: String,
    val role: String
)

// You might also want a generic error response class if the API returns structured errors
@Serializable
data class ApiErrorResponse(
    val message: String? = null, // Adjust based on actual error structure
    val errors: List<String>? = null
)