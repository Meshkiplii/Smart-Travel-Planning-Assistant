package com.meshkipli.smarttravel.data.repository

import com.meshkipli.smarttravel.data.remote.DestinationsApiResponse
import com.meshkipli.smarttravel.data.remote.KtorClient
import com.meshkipli.smarttravel.data.remote.TourDto
import com.meshkipli.smarttravel.data.remote.ToursApiResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode


class TourRepository {
    private val client = KtorClient.httpClient
    private val baseUrl = "https://tours-backend-elvinega2959-ev1ay7cf.leapcell.dev/api"


    suspend fun getTourById(tourId: String): TourDto? {
        return try {
            client.get("$baseUrl/tours/$tourId").body<TourDto>()
        } catch (e: Exception) {
            // Log the exception or handle it more gracefully
            println("Error fetching tour by ID $tourId: ${e.message}")
            null
        }
    }
    suspend fun getAllTours(): NetworkResult<ToursApiResponse> {
        return try {
            val response = client.get("$baseUrl/tours") {
                // You can add parameters here if needed, e.g., for pagination
                // parameter("page", 1)
                // parameter("limit", 10)
            }

            if (response.status == HttpStatusCode.OK) {
                NetworkResult.Success(response.body<ToursApiResponse>())
            } else {
                NetworkResult.Error(response.status, "Failed to fetch tours: ${response.status.description}")
            }
        } catch (e: ClientRequestException) {
            println("ClientRequestException on Get Tours: ${e.response.status} - ${e.message}")
            NetworkResult.Error(e.response.status, e.message ?: "Client error fetching tours")
        } catch (e: NoTransformationFoundException) {
            println("NoTransformationFoundException on Get Tours: ${e.message}")
            NetworkResult.Exception(IllegalStateException("Failed to parse tours response: ${e.message}", e))
        } catch (e: Exception) {
            println("Generic Exception in getAllTours: ${e.message}")
            NetworkResult.Exception(e)
        }
    }


    suspend fun getAllDestinations(): NetworkResult<DestinationsApiResponse> {
        return try {
            val response: DestinationsApiResponse = client.get("https://tours-backend-elvinega2959-ev1ay7cf.leapcell.dev/api/destinations").body()
            NetworkResult.Success(response)
        } catch (e: ClientRequestException) {
            // Error handling for 4xx/5xx responses if you have a specific error body
            NetworkResult.Error(code = HttpStatusCode.fromValue(e.response.status.value), message = e.message ?: "Client error")
        } catch (e: ServerResponseException) {
            NetworkResult.Error(code = HttpStatusCode.fromValue(e.response.status.value), message = "Server error: ${e.message}")
        } catch (e: NoTransformationFoundException) {
            // This can happen if the JSON structure doesn't match your DTOs
            NetworkResult.Exception(RuntimeException("JSON parsing error: ${e.localizedMessage}", e))
        } catch (e: Exception) {
            // For other errors like network issues, UnknownHostException, etc.
            NetworkResult.Exception(e)
        }
    }
}
