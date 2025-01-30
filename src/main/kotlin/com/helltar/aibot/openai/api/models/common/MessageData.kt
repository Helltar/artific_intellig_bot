package com.helltar.aibot.openai.api.models.common

import kotlinx.serialization.Serializable

@Serializable
data class MessageData(
    val role: String,
    val content: String
)
