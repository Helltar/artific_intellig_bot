package com.helltar.aibot.openai.service

import com.helltar.aibot.openai.ApiClient
import com.helltar.aibot.openai.ApiConfig.ChatContentType
import com.helltar.aibot.openai.ApiConfig.ChatRole
import com.helltar.aibot.openai.ApiConfig.Endpoints
import com.helltar.aibot.openai.Serialization.encodeToJsonString
import com.helltar.aibot.openai.Serialization.jsonDecodeFromString
import com.helltar.aibot.openai.models.common.ImageData
import com.helltar.aibot.openai.models.image.VisionContentData
import com.helltar.aibot.openai.models.image.VisionMessageData
import com.helltar.aibot.openai.models.image.VisionRequestData
import com.helltar.aibot.openai.models.image.VisionResponseData
import java.io.File
import java.util.*

class VisionService(private val apiClient: ApiClient, private val model: String) {

    fun analyzeImage(text: String, image: File): String {
        val imageBase64 = Base64.getEncoder().encodeToString(image.readBytes())
        val imageData = ImageData("data:image/jpeg;base64,$imageBase64")
        val contentTextData = VisionContentData(ChatContentType.TEXT, text)
        val contentImageData = VisionContentData(ChatContentType.IMAGE_URL, imageUrl = imageData)
        val requestData = VisionRequestData(model, listOf(VisionMessageData(ChatRole.USER, listOf(contentTextData, contentImageData))))

        val json = apiClient.postAsString(Endpoints.CHAT_COMPLETIONS, encodeToJsonString(requestData))
        val response: VisionResponseData = jsonDecodeFromString(json)

        return response.choices.first().message.content
    }
}
