package com.helltar.aibot.openai.service

import com.github.kittinunf.fuel.core.FileDataPart
import com.helltar.aibot.openai.ApiClient
import com.helltar.aibot.openai.ApiConfig.Endpoints
import com.helltar.aibot.openai.ApiConfig.Models.IMAGE_GENERATION
import com.helltar.aibot.openai.Serialization.encodeToJsonString
import com.helltar.aibot.openai.Serialization.jsonDecodeFromString
import com.helltar.aibot.openai.models.image.DalleGenerationsRequest
import com.helltar.aibot.openai.models.image.DalleResponseData
import java.io.ByteArrayOutputStream
import java.io.File

class DalleService(private val apiClient: ApiClient) {

    fun generateImage(prompt: String, size: Int = 1024): String {
        val body = encodeToJsonString(DalleGenerationsRequest(IMAGE_GENERATION, prompt, n = 1, size = "${size}x${size}"))
        val json = apiClient.postAsString(Endpoints.Images.GENERATIONS, body)
        val response: DalleResponseData = jsonDecodeFromString(json)
        return response.data.first().url
    }

    /* https://platform.openai.com/docs/api-reference/images/createVariation (dall-e-2 only) */

    fun generateImageVariations(imageData: ByteArrayOutputStream, imageFormat: String, size: Int = 256): String {
        val parameters = listOf("n" to "1", "size" to "${size}x${size}")
        val file = File.createTempFile("tmp", ".$imageFormat").apply { writeBytes(imageData.toByteArray()) }

        return try {
            val json = apiClient.uploadWithFile(Endpoints.Images.VARIATIONS, parameters, FileDataPart(file, "image"))
            val response: DalleResponseData = jsonDecodeFromString(json)
            response.data.first().url
        } finally {
            file.delete()
        }
    }
}
