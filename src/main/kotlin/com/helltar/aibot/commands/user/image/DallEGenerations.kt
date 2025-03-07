package com.helltar.aibot.commands.user.image

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.service.DalleService
import io.github.oshai.kotlinlogging.KotlinLogging

class DallEGenerations(ctx: MessageContext) : BotCommand(ctx) {

    private companion object {
        val log = KotlinLogging.logger {}
    }

    override suspend fun run() {
        if (arguments.isEmpty()) {
            replyToMessage(Strings.EMPTY_ARGS)
            return
        }

        if (argumentsString.length > 1000) {
            replyToMessage(String.format(Strings.MANY_CHARACTERS, 1000))
            return
        }

        try {
            val imageUrl = DalleService(OpenAiClient(openaiKey())).generateImageFromPrompt(argumentsString).also { log.debug { it } }
            replyToMessageWithPhoto(imageUrl, argumentsString)
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.CHAT_EXCEPTION)
        }
    }

    override fun getCommandName() =
        Commands.User.CMD_DALLE
}
