package com.helltar.aibot.commands.base

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.openai.ApiConfig

abstract class AiCommand(ctx: MessageContext) : BotCommand(ctx) {

    protected fun chatModel() =
        ApiConfig.Models.CHAT

    protected fun visionModel() =
        ApiConfig.Models.VISION
}
