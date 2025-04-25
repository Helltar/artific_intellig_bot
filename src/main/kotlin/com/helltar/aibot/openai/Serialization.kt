package com.helltar.aibot.openai

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Serialization {

    val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
        }

    internal inline fun <reified T> encodeToJsonString(obj: T) =
        json.encodeToString(obj)

    inline fun <reified T> jsonDecodeFromString(jsonString: String): T =
        try {
            json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            throw Exception("[jsonDecode] ${e.message}: $jsonString")
        }
}
