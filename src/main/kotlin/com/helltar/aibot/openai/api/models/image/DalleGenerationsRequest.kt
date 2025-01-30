package com.helltar.aibot.openai.api.models.image

import com.helltar.aibot.openai.api.ApiConfig.DALLE_GENERATIONS_IMAGE_SIZE
import com.helltar.aibot.openai.api.ApiConfig.DALLE_GENERATIONS_MODEL
import kotlinx.serialization.Serializable

/* https://platform.openai.com/docs/api-reference/images */

@Serializable
data class DalleGenerationsRequest(
    val model: String = DALLE_GENERATIONS_MODEL,
    val prompt: String,
    val n: Int = 1,
    val size: String = DALLE_GENERATIONS_IMAGE_SIZE
)
