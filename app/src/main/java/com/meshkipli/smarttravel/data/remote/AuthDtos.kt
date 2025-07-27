package com.meshkipli.smarttravel.data.remote // Or your preferred package structure

import kotlinx.serialization.Serializable

// --- Registration Data Classes (already defined) ---
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

// --- Login Data Classes ---
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val status: String,
    val message: String,
    val token: String,
    val user: UserDto // Reusing the UserDto from registration
)

// --- Shared Data Classes (already defined) ---
@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val username: String,
    val role: String
)

@Serializable
data class ApiErrorResponse(
    val message: String? = null,
    val errors: List<String>? = null
)
