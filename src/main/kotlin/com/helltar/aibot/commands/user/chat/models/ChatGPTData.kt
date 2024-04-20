package com.helltar.aibot.commands.user.chat.models

object ChatGPTData {

    const val CHAT_GPT_MODEL_3_5 = "gpt-3.5-turbo"
    const val CHAT_GPT_MODEL_4 = "gpt-4-turbo"

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
}