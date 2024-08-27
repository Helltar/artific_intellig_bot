package com.helltar.aibot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Config.PROVIDER_OPENAI_COM

abstract class OpenAICommand(ctx: MessageContext) : BotCommand(ctx) {

    protected suspend fun createAuthHeader() =
        mapOf("Authorization" to "Bearer ${getApiKey(PROVIDER_OPENAI_COM)}")

    protected suspend fun createOpenAIHeaders() =
        mapOf("Content-Type" to "application/json") + createAuthHeader()
}