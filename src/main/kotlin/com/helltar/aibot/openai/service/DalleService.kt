package com.helltar.aibot.openai.service

import com.helltar.aibot.openai.ApiClient
import com.helltar.aibot.openai.ApiConfig.Endpoints
import com.helltar.aibot.openai.ApiConfig.Models.IMAGE_GENERATION
import com.helltar.aibot.openai.models.image.DalleGenerationsRequest
import com.helltar.aibot.openai.models.image.DalleResponseData

class DalleService(private val apiClient: ApiClient = ApiClient) {

    suspend fun generateImage(prompt: String, size: Int = 1024): String {
        val request = DalleGenerationsRequest(IMAGE_GENERATION, prompt, n = 1, size = "${size}x${size}")
        val response: DalleResponseData = apiClient.post(Endpoints.IMAGES_GENERATIONS, request)
        return response.data.first().url
    }
}
