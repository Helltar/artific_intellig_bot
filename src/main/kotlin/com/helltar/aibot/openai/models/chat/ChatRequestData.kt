package com.helltar.aibot.openai.models.chat

import com.helltar.aibot.openai.models.common.MessageData
import kotlinx.serialization.Serializable

/* https://platform.openai.com/docs/api-reference/chat/create */

@Serializable
data class ChatRequestData(
    val model: String,
    val messages: List<MessageData>
)
