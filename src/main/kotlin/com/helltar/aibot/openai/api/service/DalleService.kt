package com.helltar.aibot.openai.api.service

import com.github.kittinunf.fuel.core.FileDataPart
import com.helltar.aibot.openai.api.ApiConfig.DALLE_GENERATIONS_API_URL
import com.helltar.aibot.openai.api.ApiConfig.DALLE_VARIATIONS_API_URL
import com.helltar.aibot.openai.api.ApiConfig.DALLE_VARIATIONS_IMAGE_SIZE
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.Serialization.json
import com.helltar.aibot.openai.api.models.image.DalleGenerationsRequest
import com.helltar.aibot.openai.api.models.image.DalleResponseData
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import java.io.ByteArrayOutputStream
import java.io.File

private val log = KotlinLogging.logger {}

class DalleService(private val apiClient: OpenAiClient) {

    fun generateImageFromPrompt(prompt: String): String {
        val body = json.encodeToString(DalleGenerationsRequest(prompt = prompt))
        val responseJson = apiClient.postAsString(DALLE_GENERATIONS_API_URL, body)
        return json.decodeFromString<DalleResponseData>(responseJson).data.first().url
    }

    /* https://platform.openai.com/docs/api-reference/images/createVariation */

    fun generateImageVariations(imageData: ByteArrayOutputStream, imageFormat: String): String {
        val parameters = listOf("n" to "1", "size" to DALLE_VARIATIONS_IMAGE_SIZE)
        val file = File.createTempFile("tmp", ".$imageFormat").apply { writeBytes(imageData.toByteArray()) }

        return try {
            val responseJson = apiClient.uploadWithFile(DALLE_VARIATIONS_API_URL, parameters, FileDataPart(file, "image"))
            log.debug { responseJson }
            json.decodeFromString<DalleResponseData>(responseJson).data.first().url
        } finally {
            file.delete()
        }
    }
}
