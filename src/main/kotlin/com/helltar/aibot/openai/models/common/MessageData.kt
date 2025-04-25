package com.helltar.aibot.openai.models.common

import kotlinx.serialization.Serializable

@Serializable
data class MessageData(
    val role: String,
    val content: String
)
