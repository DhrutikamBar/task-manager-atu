package com.atu.jira.repo

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object SupaBaseClient {

    private const val BASE_URL = "https://dfhgwthomtgpalkhlfwz.supabase.co"
    private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRmaGd3dGhvbXRncGFsa2hsZnd6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQyNTg1MzUsImV4cCI6MjA4OTgzNDUzNX0.FyXXXeTZbVC4Xm8ok8fR5gHhYrsA6PK9UUXBREw9YK4"

    val client = HttpClient {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("KtorLog: $message")
                }
            }
            level = LogLevel.ALL
        }


        defaultRequest {
            url(BASE_URL)
            headers {
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
                append("Content-Type", "application/json")
            }
        }
    }
}
