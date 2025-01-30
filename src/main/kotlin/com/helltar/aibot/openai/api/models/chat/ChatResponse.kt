package com.helltar.aibot.openai.api.models.chat

import com.helltar.aibot.openai.api.models.common.MessageData
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponseData(
    val model: String,
    val choices: List<ChatChoiceData>,
    val usage: ChatUsageData
)

@Serializable
data class ChatChoiceData(
    val index: Int,
    val message: MessageData,
    val finish_reason: String
)

@Serializable
data class ChatUsageData(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
