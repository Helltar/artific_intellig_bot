package com.helltar.aibot.commands.user.images.models

import com.helltar.aibot.commands.user.chat.models.Chat
import com.helltar.aibot.commands.user.chat.models.Chat.GPT_MODEL
import com.helltar.aibot.commands.user.chat.models.Chat.ROLE_USER
import kotlinx.serialization.Serializable

object Vision {

    /* https://platform.openai.com/docs/guides/vision */

    const val MESSAGE_CONTENT_TYPE_TEXT = "text"
    const val MESSAGE_CONTENT_TYPE_IMAGE = "image_url"

    @Serializable
    data class RequestData(
        val model: String = GPT_MODEL,
        val messages: List<MessageData>
    )

    @Serializable
    data class ResponseData(val choices: List<ChoiseData>)

    @Serializable
    data class ChoiseData(val message: Chat.MessageData)

    @Serializable
    data class MessageData(
        val role: String = ROLE_USER,
        val content: List<ContentData>
    )

    @Serializable
    data class ContentData(
        val type: String,
        val text: String? = null,
        val image_url: Dalle.ImageData? = null
    )
}
