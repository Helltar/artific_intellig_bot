package com.helltar.aibot.openai.models.image

import com.helltar.aibot.openai.models.common.MessageData
import kotlinx.serialization.Serializable

@Serializable
data class VisionResponseData(
    val choices: List<VisionChoiseData>
)

@Serializable
data class VisionChoiseData(
    val message: MessageData
)
