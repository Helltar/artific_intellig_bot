package com.helltar.aibot.commands.user.images.models

import kotlinx.serialization.Serializable

object Dalle {

    /* https://platform.openai.com/docs/api-reference/images/createVariation */

    const val DALLE_VARIATIONS_API_URL = "https://api.openai.com/v1/images/variations"
    const val DALLE_VARIATIONS_FILEDATAPART_NAME = "image"
    val dalleVariationsParams = listOf("n" to "1", "size" to "256x256")

    /* https://platform.openai.com/docs/api-reference/images */

    const val DALLE_GENERATIONS_API_URL = "https://api.openai.com/v1/images/generations"
    const val DALLE_GENERATIONS_MODEL = "dall-e-3"
    const val DALLE_GENERATIONS_IMAGE_SIZE = "1024x1024"

    @Serializable
    data class RequestData(
        val model: String = DALLE_GENERATIONS_MODEL,
        val prompt: String,
        val n: Int = 1,
        val size: String = DALLE_GENERATIONS_IMAGE_SIZE
    )

    @Serializable
    data class ResponseData(val data: List<ImageData>)

    @Serializable
    data class ImageData(val url: String)
}
