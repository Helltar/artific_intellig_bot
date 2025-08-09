package com.helltar.aibot.openai.service

import com.helltar.aibot.openai.ApiConfig.Endpoints
import com.helltar.aibot.openai.HttpClient
import com.helltar.aibot.openai.models.image.ImageGenRequest
import com.helltar.aibot.openai.models.image.ImageGenResponseData

class ImageGenService(private val client: HttpClient = HttpClient, private val model: String, private val apiKey: String) {

    suspend fun generateImage(prompt: String, size: Int = 1024): String {
        val request = ImageGenRequest(model, prompt, n = 1, size = "${size}x${size}")
        val response: ImageGenResponseData = client.post(apiKey, Endpoints.IMAGES_GENERATIONS, request)
        return response.data.first().url
    }
}
