package com.helltar.aibot.commands.user.chat.models

object ChatGPTData {

    const val CHAT_GPT_MODEL_3_5 = "gpt-4o-mini"
    const val CHAT_GPT_MODEL_4 = "gpt-4o"

    const val CHAT_ROLE_USER = "user"
    const val CHAT_ROLE_ASSISTANT = "assistant"
    const val CHAT_ROLE_SYSTEM = "system"

    data class ChatData(
        val model: String,
        val messages: List<ChatMessageData>
    )

    data class ChatMessageData(
        val role: String,
        val content: String
    )

    data class SpeechData(
        val model: String = "tts-1",
        val input: String,
        val voice: String = "nova",
        val response_format: String = "opus"
    )
}