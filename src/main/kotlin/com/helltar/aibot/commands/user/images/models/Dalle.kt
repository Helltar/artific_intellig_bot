package com.helltar.aibot.commands.user.images.models

import kotlinx.serialization.Serializable

object Dalle {

    /* https://platform.openai.com/docs/api-reference/images */

    const val DALLE_GENERATIONS_MODEL = "dall-e-3"
    const val DALLE_IMAGE_SIZE = "256x256"

    @Serializable
    data class RequestData(
        val model: String = DALLE_GENERATIONS_MODEL,
        val prompt: String,
        val n: Int = 1,
        val size: String = DALLE_IMAGE_SIZE
    )

    @Serializable
    data class ResponseData(val data: List<ImageData>)

    @Serializable
    data class ImageData(val url: String)
}
