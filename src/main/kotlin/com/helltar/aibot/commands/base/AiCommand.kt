package com.helltar.aibot.commands.base

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.database.dao.apiKeyDao
import com.helltar.aibot.openai.ApiConfig

abstract class AiCommand(ctx: MessageContext) : BotCommand(ctx) {

    protected suspend fun openaiKey(): String? =
        apiKeyDao.getKey(ApiConfig.OpenAi.PROVIDER_NAME)

    protected fun chatModel() =
        ApiConfig.Models.CHAT

    protected fun visionModel() =
        ApiConfig.Models.VISION

    protected fun ttsModel() =
        ApiConfig.Models.TTS

    protected fun transcriptionModel() =
        ApiConfig.Models.TRANSCRIPTION
}
