package com.helltar.aibot.openai.api.models.image

import com.helltar.aibot.openai.api.models.common.MessageData
import kotlinx.serialization.Serializable

@Serializable
data class VisionResponseData(
    val choices: List<VisionChoiseData>
)

@Serializable
data class VisionChoiseData(
    val message: MessageData
)
