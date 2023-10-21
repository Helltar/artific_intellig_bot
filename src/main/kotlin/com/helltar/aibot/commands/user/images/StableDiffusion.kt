package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.Gson
import com.helltar.aibot.BotConfig.PROVIDER_STABILITY_AI
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.images.models.StableDiffusionData
import com.helltar.aibot.commands.user.images.models.StableDiffusionData.ENGINE_ID
import com.helltar.aibot.commands.user.images.models.StableDiffusionData.TextPromptData
import com.helltar.aibot.utils.NetworkUtils.httpPost
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

        val response = sendPrompt(argsText, StableDiffusionData.StylePresets.PHOTOGRAPHIC)
        val responseBytes = response.data

        if (response.isSuccessful) {
            try {
                val caption = // todo: StableDiffusion caption
                    if (argsText.length > 128)
                        "${argsText.substring(0, 128)}..."
                    else
                        argsText

                File.createTempFile("tmp", ".png").apply { // todo: temp file
                    writeBytes(responseBytes)
                    replyToMessageWithPhoto(this, caption)
                    delete()
                }

                return
            } catch (e: Exception) {
                log.error(e.message)
            }
        } else
            try {
                replyToMessage(JSONObject(response.data.decodeToString()).getString("message"))
                return
            } catch (e: Exception) {
                log.error(e.message)
            }

        replyToMessage(Strings.BAD_REQUEST)
        log.error("$response: $args")
    }

    override fun getCommandName() =
        Commands.CMD_SDIFF

    private fun sendPrompt(prompt: String, stylePreset: String): Response {
        val url = "https://api.stability.ai/v1/generation/$ENGINE_ID/text-to-image"
        val headers = mapOf("Accept" to "image/png", "Authorization" to "Bearer ${getApiKey(PROVIDER_STABILITY_AI)}")
        val body = Gson().toJson(StableDiffusionData.RequestData(style_preset = stylePreset, text_prompts = listOf(TextPromptData(prompt))))
        return httpPost(url, headers, body)
    }
}