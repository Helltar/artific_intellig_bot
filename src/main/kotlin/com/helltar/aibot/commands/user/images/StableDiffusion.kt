package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File

class StableDiffusion(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun run() {
        if (arguments.isEmpty()) {
            replyToMessage(Strings.STABLE_DIFFUSION_EMPTY_ARGS)
            return
        }

        if (argumentsString.length > 2000) {
            replyToMessage(String.format(Strings.MANY_CHARACTERS, 2000))
            return
        }

        val response = sendPrompt(argumentsString)
        val responseData = response.data

        if (response.isSuccessful) {
            try {
                var caption = argumentsString

                if (caption.length > 500)
                    caption = "${caption.substring(0, 500)} ..."

                val file = // todo: temp file
                    withContext(Dispatchers.IO) {
                        File.createTempFile("tmp", ".png")
                    }.apply {
                        writeBytes(responseData)
                    }

                replyToMessageWithPhoto(file, caption)
            } catch (e: Exception) {
                log.error(e.message)
                replyToMessage(Strings.BAD_REQUEST)
            }
        } else {
            log.error(responseData.decodeToString())
            replyToMessage(Strings.BAD_REQUEST)
        }
    }

    override fun getCommandName() =
        Commands.CMD_SDIFF

    private suspend fun sendPrompt(prompt: String): Response {
        val url = "https://api.stability.ai/v2beta/stable-image/generate/sd3"
        val headers = mapOf("Accept" to "image/*", "Authorization" to "Bearer ${getApiKey(PROVIDER_STABILITY_AI)}")
        val params = listOf("prompt" to prompt, "model" to "sd3-turbo", "aspect_ratio" to "1:1", "output_format" to "jpeg")
        return NetworkUtils.upload(url, headers, params)
    }
}