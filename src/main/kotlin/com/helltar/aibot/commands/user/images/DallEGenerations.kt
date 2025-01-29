package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.OpenAICommand
import com.helltar.aibot.commands.user.images.models.Dalle
import com.helltar.aibot.commands.user.images.models.Dalle.GENERATIONS_API_URL
import com.helltar.aibot.utils.Network.postAsString
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString

private val log = KotlinLogging.logger {}

class DallEGenerations(ctx: MessageContext) : OpenAICommand(ctx) {

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
            val responseJson = sendPrompt(argumentsString).also { log.debug { it } }
            val imageUrl = json.decodeFromString<Dalle.ResponseData>(responseJson).data.first().url
            replyToMessageWithPhoto(imageUrl, argumentsString)
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.CHAT_EXCEPTION)
        }
    }

    override fun getCommandName() =
        Commands.CMD_DALLE

    private suspend fun sendPrompt(prompt: String): String {
        val body = json.encodeToString(Dalle.RequestData(prompt = prompt))
        return postAsString(GENERATIONS_API_URL, createOpenAIHeaders(), body)
    }
}
