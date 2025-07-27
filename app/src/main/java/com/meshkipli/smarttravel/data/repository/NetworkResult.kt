package com.meshkipli.smarttravel.data.repository

import com.meshkipli.smarttravel.data.remote.KtorClient
import com.meshkipli.smarttravel.data.remote.RegisterRequest
import com.meshkipli.smarttravel.data.remote.RegisterResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.ClientRequestException // Import this
import io.ktor.client.request.*
import io.ktor.http.*

// A sealed class or interface to represent the result of the API call
sealed class NetworkResult<T : Any> {
    class Success<T: Any>(val data: T) : NetworkResult<T>()
    class Error<T: Any>(val code: HttpStatusCode, val message: String?) : NetworkResult<T>()
    class Exception<T: Any>(val e: Throwable) : NetworkResult<T>()
}


class AuthRepository {
    private val client = KtorClient.httpClient // Use the configured client

    suspend fun registerUser(request: RegisterRequest): NetworkResult<RegisterResponse> {
        return try {
            val response = client.post("https://tours-backend-elvinega2959-ev1ay7cf.leapcell.dev/api/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.Created) { // Status 201
                NetworkResult.Success(response.body<RegisterResponse>())
            } else {
                // For non-2xx responses, Ktor might throw ClientRequestException if expectSuccess is true
                // or if you try to .body() directly without checking status first.
                // The way it's structured now, this 'else' block should be hit for non-201.
                NetworkResult.Error(response.status, "Registration failed: ${response.status.description}")
            }
        } catch (e: ClientRequestException) {
            // This exception is typically thrown for 4xx/5xx responses if you try to read the body
            // or if `expectSuccess = true` is set (which is false by default).
            // The response object is available in e.response
            println("ClientRequestException: ${e.response.status} - ${e.message}")
            NetworkResult.Error(e.response.status, e.message)
        } catch (e: NoTransformationFoundException) {
            // This can happen if Ktor cannot deserialize the response body into RegisterResponse,
            // e.g., if the JSON structure is unexpected, or if the content type is not application/json
            // for a successful response.
            println("NoTransformationFoundException: ${e.message}")
            NetworkResult.Exception(IllegalStateException("Failed to parse response: ${e.message}", e))
        }
        // Catch other Ktor specific exceptions if needed, or more general ones.
        // For instance, io.ktor.client.plugins.HttpRequestTimeoutException
        catch (e: Exception) { // Catch more general exceptions like network issues, other serialization errors
            println("Generic Exception in registerUser: ${e.message}")
            NetworkResult.Exception(e)
        }
    }
}
   