package com.helltar.aibot.openai.api.service

import com.github.kittinunf.fuel.core.FileDataPart
import com.helltar.aibot.openai.api.ApiConfig.DALLE_GENERATIONS_API_URL
import com.helltar.aibot.openai.api.ApiConfig.DALLE_VARIATIONS_API_URL
import com.helltar.aibot.openai.api.ApiConfig.DALLE_VARIATIONS_IMAGE_SIZE
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.Serialization.encodeToJsonString
import com.helltar.aibot.openai.api.Serialization.jsonDecodeFromString
import com.helltar.aibot.openai.api.models.image.DalleGenerationsRequest
import com.helltar.aibot.openai.api.models.image.DalleResponseData
import java.io.ByteArrayOutputStream
import java.io.File

class DalleService(private val apiClient: OpenAiClient) {

    fun generateImageFromPrompt(prompt: String): String {
        val body = encodeToJsonString(DalleGenerationsRequest(prompt = prompt))
        val json = apiClient.postAsString(DALLE_GENERATIONS_API_URL, body)
        val response: DalleResponseData = jsonDecodeFromString(json)
        return response.data.first().url
    }

    /* https://platform.openai.com/docs/api-reference/images/createVariation */

    fun generateImageVariations(imageData: ByteArrayOutputStream, imageFormat: String): String {
        val parameters = listOf("n" to "1", "size" to DALLE_VARIATIONS_IMAGE_SIZE)
        val file = File.createTempFile("tmp", ".$imageFormat").apply { writeBytes(imageData.toByteArray()) }

        return try {
            val json = apiClient.uploadWithFile(DALLE_VARIATIONS_API_URL, parameters, FileDataPart(file, "image"))
            val response: DalleResponseData = jsonDecodeFromString(json)
            response.data.first().url
        } finally {
            file.delete()
        }
    }
}
