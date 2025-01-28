package com.helltar.aibot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Config.PROVIDER_OPENAI

abstract class OpenAICommand(ctx: MessageContext) : BotCommand(ctx) {

    protected suspend fun createAuthHeader(provider: String = PROVIDER_OPENAI) =
        mapOf("Authorization" to "Bearer ${getApiKey(provider)}")

    protected suspend fun createOpenAIHeaders(provider: String = PROVIDER_OPENAI) =
        mapOf("Content-Type" to "application/json") + createAuthHeader(provider)
}
