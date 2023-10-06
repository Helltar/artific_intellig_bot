package com.helltar.artific_intellig_bot.commands.user.chat.models

object ChatGPTData {

    const val CHAT_GPT_MODEL = "gpt-3.5-turbo"
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