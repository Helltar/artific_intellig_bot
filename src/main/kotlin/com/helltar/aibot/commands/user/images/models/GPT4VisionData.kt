package com.helltar.aibot.commands.user.images.models

import com.helltar.aibot.commands.user.chat.models.ChatGPTData
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_GPT_MODEL_4_MINI
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_USER

object GPT4VisionData {

    const val MESSAGE_CONTENT_TYPE_TEXT = "text"
    const val MESSAGE_CONTENT_TYPE_IMAGE = "image_url"

    data class RequestData(
        val model: String = CHAT_GPT_MODEL_4_MINI,
        val messages: List<MessageData>,
        val max_tokens: Int = 600
    )

    data class ResponseData(
        val choices: List<ChoiseData>
    )

    data class ChoiseData(
        val message: ChatGPTData.ChatMessageData
    )

    data class MessageData(
        val role: String = CHAT_ROLE_USER,
        val content: List<ContentData>
    )

    data class ContentData(
        val type: String,
        val text: String? = null,
        val image_url: ImageData? = null
    )

    data class ImageData(
        val url: String
    )
}