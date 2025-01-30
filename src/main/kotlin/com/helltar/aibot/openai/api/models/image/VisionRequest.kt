package com.helltar.aibot.openai.api.models.image

import com.helltar.aibot.openai.api.models.common.ImageData
import kotlinx.serialization.Serializable

/* https://platform.openai.com/docs/guides/vision */

@Serializable
data class VisionRequestData(
    val model: String,
    val messages: List<VisionMessageData>
)

@Serializable
data class VisionMessageData(
    val role: String,
    val content: List<VisionContentData>
)

@Serializable
data class VisionContentData(
    val type: String,
    val text: String? = null,
    val image_url: ImageData? = null
)
