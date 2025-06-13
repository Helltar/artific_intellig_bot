package com.helltar.aibot.openai

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

object ApiClient {

    private const val DEFAULT_TIMEOUT = 60_000L

    private var httpClient: HttpClient? = null
    private var apiKey: String? = null
    private val mutex = Mutex()

    suspend fun configure(newApiKey: String?) = mutex.withLock {
        if (apiKey == newApiKey)
            return

        apiKey = newApiKey

        httpClient?.close()
        httpClient = createHttpClient()
    }

    suspend fun client(): HttpClient {
        httpClient?.let { return it }

        return mutex.withLock {
            httpClient ?: createHttpClient().also { httpClient = it }
        }
    }

    suspend inline fun <reified T> post(endpoint: String, request: Any): T =
        client()
            .post(ApiConfig.BASE_URL + endpoint) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()

    private fun createHttpClient(): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        explicitNulls = false
                    }
                )
            }

            install(Auth) {
                bearer {
                    loadTokens { BearerTokens(apiKey ?: "", null) }
                    sendWithoutRequest { true }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = DEFAULT_TIMEOUT
                connectTimeoutMillis = DEFAULT_TIMEOUT
                socketTimeoutMillis = DEFAULT_TIMEOUT
            }
        }
    }
}
