package com.helltar.aibot.openai.models.image

import com.helltar.aibot.openai.models.common.ImageData
import kotlinx.serialization.Serializable

@Serializable
data class DalleResponseData(
    val data: List<ImageData>
)
