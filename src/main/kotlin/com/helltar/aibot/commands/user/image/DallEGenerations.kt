package com.helltar.aibot.commands.user.image

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.AiCommand
import com.helltar.aibot.openai.ApiClient
import com.helltar.aibot.openai.service.DalleService
import io.github.oshai.kotlinlogging.KotlinLogging

class DallEGenerations(ctx: MessageContext) : AiCommand(ctx) {

    private companion object {
        val log = KotlinLogging.logger {}
    }

    override suspend fun run() {
        if (arguments.isEmpty()) {
            replyToMessage(Strings.DALLE_COMMAND_USAGE_TEMPLATE_RAW.trimIndent())
            return
        }

        if (argumentsString.length > 1000) {
            replyToMessage(String.format(Strings.MANY_CHARACTERS, 1000))
            return
        }

        try {
            val imageUrl = DalleService(ApiClient(openaiKey())).generateImage(argumentsString)
            log.debug { "image url: $imageUrl" }
            replyToMessageWithPhoto(imageUrl, argumentsString)
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.CHAT_EXCEPTION)
        }
    }

    override fun commandName() =
        Commands.User.CMD_DALLE
}
