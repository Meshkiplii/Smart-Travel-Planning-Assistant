package com.meshkipli.smarttravel.data.repository

import com.meshkipli.smarttravel.data.remote.KtorClient
import com.meshkipli.smarttravel.data.remote.TourDto
import com.meshkipli.smarttravel.data.remote.ToursApiResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*


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
}
