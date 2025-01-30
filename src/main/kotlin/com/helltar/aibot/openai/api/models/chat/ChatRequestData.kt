package com.helltar.aibot.openai.api.models.chat

import com.helltar.aibot.openai.api.models.common.MessageData
import kotlinx.serialization.Serializable

/*
  https://platform.openai.com/docs/api-reference/chat/create
  https://api-docs.deepseek.com/api/create-chat-completion
*/

@Serializable
data class ChatRequestData(
    val model: String,
    val messages: List<MessageData>
)
