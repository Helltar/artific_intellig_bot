package com.helltar.aibot.openai.api.models.audio

import kotlinx.serialization.Serializable

/* https://platform.openai.com/docs/api-reference/audio/createSpeech */

@Serializable
data class SpeechRequest(
    val model: String = "tts-1",
    val input: String,
    val voice: String = "nova",
    val response_format: String = "opus"
)
