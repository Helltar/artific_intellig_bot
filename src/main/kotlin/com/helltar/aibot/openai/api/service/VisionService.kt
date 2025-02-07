package com.helltar.aibot.openai.api.service

import com.helltar.aibot.openai.api.ApiConfig.CHAT_MODEL
import com.helltar.aibot.openai.api.ApiConfig.CHAT_ROLE_USER
import com.helltar.aibot.openai.api.ApiConfig.COMPLETIONS_API_URL
import com.helltar.aibot.openai.api.ApiConfig.MESSAGE_CONTENT_TYPE_IMAGE
import com.helltar.aibot.openai.api.ApiConfig.MESSAGE_CONTENT_TYPE_TEXT
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.Serialization.encodeToJsonString
import com.helltar.aibot.openai.api.Serialization.jsonDecodeFromString
import com.helltar.aibot.openai.api.models.common.ImageData
import com.helltar.aibot.openai.api.models.image.VisionContentData
import com.helltar.aibot.openai.api.models.image.VisionMessageData
import com.helltar.aibot.openai.api.models.image.VisionRequestData
import com.helltar.aibot.openai.api.models.image.VisionResponseData
import java.io.File
import java.util.*

class VisionService(private val apiClient: OpenAiClient) {

    fun analyzeImage(text: String, image: File): String {
        val imageBase64 = Base64.getEncoder().encodeToString(image.readBytes())
        val imageData = ImageData("data:image/jpeg;base64,$imageBase64")
        val contentTextData = VisionContentData(MESSAGE_CONTENT_TYPE_TEXT, text)
        val contentImageData = VisionContentData(MESSAGE_CONTENT_TYPE_IMAGE, imageUrl = imageData)
        val requestData = VisionRequestData(CHAT_MODEL, listOf(VisionMessageData(CHAT_ROLE_USER, listOf(contentTextData, contentImageData))))

        val json = apiClient.postAsString(COMPLETIONS_API_URL, encodeToJsonString(requestData))
        val response: VisionResponseData = jsonDecodeFromString(json)

        return response.choices.first().message.content
    }
}
