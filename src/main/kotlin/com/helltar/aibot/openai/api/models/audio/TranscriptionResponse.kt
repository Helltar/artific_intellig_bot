package com.helltar.aibot.openai.api.models.audio

import kotlinx.serialization.Serializable

@Serializable
data class TranscriptionResponse(
    val text: String
)
