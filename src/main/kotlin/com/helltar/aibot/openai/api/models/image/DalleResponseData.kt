package com.helltar.aibot.openai.api.models.image

import com.helltar.aibot.openai.api.models.common.ImageData
import kotlinx.serialization.Serializable

@Serializable
data class DalleResponseData(
    val data: List<ImageData>
)
