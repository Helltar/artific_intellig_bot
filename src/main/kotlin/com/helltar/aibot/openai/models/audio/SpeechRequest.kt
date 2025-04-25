package com.helltar.aibot.openai.models.audio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* https://platform.openai.com/docs/api-reference/audio/createSpeech */

@Serializable
data class SpeechRequest(
    val model: String,
    val input: String,
    val voice: String,

    @SerialName("response_format")
    val responseFormat: String
)
