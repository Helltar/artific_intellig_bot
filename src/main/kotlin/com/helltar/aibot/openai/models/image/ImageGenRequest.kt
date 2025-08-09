package com.helltar.aibot.openai.models.image

import kotlinx.serialization.Serializable

/* https://platform.openai.com/docs/api-reference/images */

@Serializable
data class ImageGenRequest(
    val model: String,
    val prompt: String,
    val n: Int,
    val size: String
)
