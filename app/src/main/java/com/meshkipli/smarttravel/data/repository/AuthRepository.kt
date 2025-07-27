package com.meshkipli.smarttravel.data.repository

import com.meshkipli.smarttravel.data.remote.ApiErrorResponse
import com.meshkipli.smarttravel.data.remote.KtorClient
import com.meshkipli.smarttravel.data.remote.LoginRequest // New import
import com.meshkipli.smarttravel.data.remote.LoginResponse // New import
import com.meshkipli.smarttravel.data.remote.RegisterRequest
import com.meshkipli.smarttravel.data.remote.RegisterResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.*
import io.ktor.http.*

// NetworkResult sealed class (already defined)
// sealed class NetworkResult<T : Any> { ... }


class AuthRepository {
    private val client = KtorClient.httpClient

    suspend fun registerUser(request: RegisterRequest): NetworkResult<RegisterResponse> {
        // ... (existing registerUser code)
        return try {
            val response = client.post("https://tours-backend-elvinega2959-ev1ay7cf.leapcell.dev/api/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.Created) {
                NetworkResult.Success(response.body<RegisterResponse>())
            } else {
                NetworkResult.Error(response.status, "Registration failed: ${response.status.description}")
            }
        } catch (e: ClientRequestException) {
            println("ClientRequestException on Register: ${e.response.status} - ${e.message}")
            NetworkResult.Error(e.response.status, e.message ?: "Client request error during registration")
        } catch (e: NoTransformationFoundException) {
            println("NoTransformationFoundException on Register: ${e.message}")
            NetworkResult.Exception(IllegalStateException("Failed to parse registration response: ${e.message}", e))
        }
        catch (e: Exception) {
            println("Generic Exception in registerUser: ${e.message}")
            NetworkResult.Exception(e)
        }
    }

    // --- New Login Function ---
    suspend fun loginUser(request: LoginRequest): NetworkResult<LoginResponse> {
        return try {
            val response = client.post("https://tours-backend-elvinega2959-ev1ay7cf.leapcell.dev/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            // The API returns 200 for login according to your spec, which is unusual (typically 200 OK).
            // Let's stick to your spec.
            if (response.status == HttpStatusCode.OK) {
                val loginData = response.body<LoginResponse>()
                if (loginData.status == "success") {
                    NetworkResult.Success(loginData)
                } else {
                    // If the outer status is 201 but inner JSON status is not "success"
                    NetworkResult.Error(response.status, loginData.message ?: "Login failed, unknown reason from API.")
                }
            } else {
                 // Attempt to parse a standard error message if not 201
                val errorMessage = try { response.body<ApiErrorResponse>().message } catch (e: Exception) { response.status.description }
                NetworkResult.Error(response.status, "Login failed: ${errorMessage}")
            }
        } catch (e: ClientRequestException) {
            // This catches non-2xx responses if Ktor tries to process them as errors before the 'else' block.
            // Or if body() is called on a non-2xx response.
            val errorMessage = try { e.response.body<ApiErrorResponse>().message } catch (ex: Exception) { e.message }
            println("ClientRequestException on Login: ${e.response.status} - $errorMessage")
            NetworkResult.Error(e.response.status, errorMessage ?: "Client request error during login")
        } catch (e: NoTransformationFoundException) {
            // If the body can't be parsed into LoginResponse or ApiErrorResponse
            println("NoTransformationFoundException on Login: ${e.message}")
            NetworkResult.Exception(IllegalStateException("Failed to parse login response: ${e.message}", e))
        }
        catch (e: Exception) { // General network or other unexpected errors
            println("Generic Exception in loginUser: ${e.message}")
            NetworkResult.Exception(e)
        }
    }
}

