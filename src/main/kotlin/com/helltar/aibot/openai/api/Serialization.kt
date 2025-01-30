package com.helltar.aibot.openai.api

import kotlinx.serialization.json.Json

object Serialization {

    val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
        }
}
