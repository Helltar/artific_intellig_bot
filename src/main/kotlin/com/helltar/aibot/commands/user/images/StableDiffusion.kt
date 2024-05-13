package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.helltar.aibot.BotConfig.PROVIDER_STABILITY_AI
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.utils.NetworkUtils.httpUpload
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File

class StableDiffusion(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (args.isEmpty()) {
            replyToMessage(Strings.STABLE_DIFFUSION_EMPTY_ARGS)
            return
        }

        if (argsText.length > 2000) {
            replyToMessage(String.format(Strings.MANY_CHARACTERS, 2000))
            return
        }

        val response = sendPrompt(argsText)
        val responseData = response.data

        if (response.isSuccessful) {
            try {
                var caption = argsText

                if (caption.length > 512)
                    caption = caption.substring(0, 512)

                File.createTempFile("tmp", ".png").apply { // todo: temp file
                    writeBytes(responseData)
                    replyToMessageWithPhoto(this, caption)
                    delete()
                }
            } catch (e: Exception) {
                log.error(e.message)
                replyToMessage(Strings.BAD_REQUEST)
            }
        } else
            try {
                val jsonObject = JSONObject(responseData.decodeToString())
                val errors = jsonObject.getJSONArray("errors")
                replyToMessage(errors.first().toString())
            } catch (e: Exception) {
                log.error(e.message)
                replyToMessage(Strings.BAD_REQUEST)
            }
    }

    override fun getCommandName() =
        Commands.CMD_SDIFF

    private fun sendPrompt(prompt: String): Response {
        val url = "https://api.stability.ai/v2beta/stable-image/generate/sd3"
        val headers = mapOf("Accept" to "image/*", "Authorization" to "Bearer ${getApiKey(PROVIDER_STABILITY_AI)}")
        val params = listOf("prompt" to prompt, "model" to "sd3-turbo", "aspect_ratio" to "1:1", "output_format" to "jpeg")
        return httpUpload(url, headers, params)
    }
}