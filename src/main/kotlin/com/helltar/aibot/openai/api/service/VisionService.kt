package com.helltar.aibot.openai.api.service

import com.helltar.aibot.openai.api.ApiConfig.CHAT_MODEL
import com.helltar.aibot.openai.api.ApiConfig.CHAT_ROLE_USER
import com.helltar.aibot.openai.api.ApiConfig.COMPLETIONS_API_URL
import com.helltar.aibot.openai.api.ApiConfig.MESSAGE_CONTENT_TYPE_IMAGE
import com.helltar.aibot.openai.api.ApiConfig.MESSAGE_CONTENT_TYPE_TEXT
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.Serialization.json
import com.helltar.aibot.openai.api.models.common.ImageData
import com.helltar.aibot.openai.api.models.image.VisionContentData
import com.helltar.aibot.openai.api.models.image.VisionMessageData
import com.helltar.aibot.openai.api.models.image.VisionRequestData
import com.helltar.aibot.openai.api.models.image.VisionResponseData
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import java.io.File
import java.util.*

private val log = KotlinLogging.logger {}

class VisionService(private val apiClient: OpenAiClient) {

    fun analyzeImage(text: String, image: File): String {
        val imageBase64 = Base64.getEncoder().encodeToString(image.readBytes())
        val imageData = ImageData("data:image/jpeg;base64,$imageBase64")
        val contentTextData = VisionContentData(MESSAGE_CONTENT_TYPE_TEXT, text)
        val contentImageData = VisionContentData(MESSAGE_CONTENT_TYPE_IMAGE, imageUrl = imageData)
        val requestData = VisionRequestData(CHAT_MODEL, listOf(VisionMessageData(CHAT_ROLE_USER, listOf(contentTextData, contentImageData))))

        val responseJson = apiClient.postAsString(COMPLETIONS_API_URL, json.encodeToString(requestData))

        log.debug { responseJson }

        return json.decodeFromString<VisionResponseData>(responseJson).choices.first().message.content
    }
}
