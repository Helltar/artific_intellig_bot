package com.helltar.aibot.commands.user.images.models

import com.helltar.aibot.commands.user.chat.models.Chat
import com.helltar.aibot.commands.user.chat.models.Chat.CHAT_GPT_MODEL_4_MINI
import com.helltar.aibot.commands.user.chat.models.Chat.CHAT_ROLE_USER
import kotlinx.serialization.Serializable

object Vision {

    const val MESSAGE_CONTENT_TYPE_TEXT = "text"
    const val MESSAGE_CONTENT_TYPE_IMAGE = "image_url"

    @Serializable
    data class RequestData(
        val model: String = CHAT_GPT_MODEL_4_MINI,
        val messages: List<MessageData>,
        val max_tokens: Int = 600
    )

    @Serializable
    data class ResponseData(val choices: List<ChoiseData>)

    @Serializable
    data class ChoiseData(val message: Chat.MessageData)

    @Serializable
    data class MessageData(
        val role: String = CHAT_ROLE_USER,
        val content: List<ContentData>
    )

    @Serializable
    data class ContentData(
        val type: String,
        val text: String? = null,
        val image_url: Dalle.ImageData? = null
    )
}