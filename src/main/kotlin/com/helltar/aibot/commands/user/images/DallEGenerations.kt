package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.OpenAICommand
import com.helltar.aibot.commands.user.images.models.Dalle
import com.helltar.aibot.utils.NetworkUtils.postJson
import kotlinx.serialization.encodeToString
import org.slf4j.LoggerFactory

class DallEGenerations(ctx: MessageContext) : OpenAICommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

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
            val responseJson = sendPrompt(argumentsString)
            val url = json.decodeFromString<Dalle.ResponseData>(responseJson).data.first().url
            replyToMessageWithPhoto(url, argumentsString)
        } catch (e: Exception) {
            replyToMessage(Strings.CHAT_EXCEPTION)
            log.error(e.message)
        }
    }

    override fun getCommandName() =
        Commands.CMD_DALLE

    private suspend fun sendPrompt(prompt: String): String {
        val url = "https://api.openai.com/v1/images/generations"
        val body = json.encodeToString(Dalle.RequestData(prompt))
        return postJson(url, createOpenAIHeaders(), body).data.decodeToString()
    }
}
