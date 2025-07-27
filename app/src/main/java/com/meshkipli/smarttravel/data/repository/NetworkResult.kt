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
