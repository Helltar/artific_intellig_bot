package com.helltar.aibot.openai.api.models.chat

import com.helltar.aibot.openai.api.models.common.MessageData
import kotlinx.serialization.SerialName
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

    @SerialName("finish_reason")
    val finishReason: String
)

@Serializable
data class ChatUsageData(

    @SerialName("prompt_tokens")
    val promptTokens: Int,

    @SerialName("completion_tokens")
    val completionTokens: Int,

    @SerialName("total_tokens")
    val totalTokens: Int
)
