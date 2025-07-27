package com.meshkipli.smarttravel.data.remote

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    val httpClient: HttpClient = HttpClient(Android) { // Or HttpClient(Android) or HttpClient(OkHttp)
        // Configure JSON serialization
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Important for API evolution
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000  // 15 seconds timeout (adjust as needed)
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }

        // Optional: Logging for debugging (Add ktor-client-logging dependency if you use this)
//        install(Logging) {
//            logger = Logger.DEFAULT // Or a custom logger: object : Logger { override fun log(message: String) { Log.v("Ktor", message) } }
//            level = LogLevel.ALL // Log headers, body, etc. Use LogLevel.INFO or LogLevel.NONE in production.
//        }

        // Optional: Default request configuration (e.g., base URL, headers)
        // defaultRequest {
        //    url("https://tours-backend-elvinega2959-ev1ay7cf.leapcell.dev/api/")
        //    contentType(ContentType.Application.Json)
        // }
    }
}
