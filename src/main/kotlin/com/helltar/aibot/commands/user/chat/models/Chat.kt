package com.helltar.aibot.commands.user.chat.models

import kotlinx.serialization.Serializable

object Chat {

    const val CHAT_GPT_MODEL_4 = "gpt-4o"
    const val CHAT_GPT_MODEL_4_MINI = "gpt-4o-mini"

    const val CHAT_ROLE_USER = "user"
    const val CHAT_ROLE_ASSISTANT = "assistant"
    const val CHAT_ROLE_SYSTEM = "system"

    @Serializable
    data class RequestData(
        val model: String,
        val messages: List<MessageData>
    )

    @Serializable
    data class ResponseData(
        val model: String,
        val choices: List<ChoiceData>,
        val usage: UsageData
    )

    @Serializable
    data class MessageData(
        val role: String,
        val content: String
    )

    @Serializable
    data class ChoiceData(
        val index: Int,
        val message: MessageData,
        val finish_reason: String
    )

    @Serializable
    data class UsageData(
        val prompt_tokens: Int,
        val completion_tokens: Int,
        val total_tokens: Int
    )

    @Serializable
    data class SpeechRequestData(
        val model: String = "tts-1",
        val input: String,
        val voice: String = "nova",
        val response_format: String = "opus"
    )
}