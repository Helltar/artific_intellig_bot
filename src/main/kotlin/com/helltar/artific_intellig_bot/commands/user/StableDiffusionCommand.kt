package com.helltar.artific_intellig_bot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

class StableDiffusionCommand(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    private companion object {
        const val ENGINE_ID = "stable-diffusion-xl-1024-v1-0"
    }

    override fun run() {
        if (args.isEmpty()) {
            replyToMessage(Strings.stable_diffusion_empty_args)
            return
        }

        if (argsText.length > 2000) {
            replyToMessage(String.format(Strings.many_characters, 2000))
            return
        }

        val response = sendPrompt(argsText)
        val responseData = response.second.data.decodeToString()

        if (response.second.isSuccessful) {
            try {
                val base64 = JSONObject(responseData).getJSONArray("artifacts").getJSONObject(0).getString("base64")
                // todo: tempFile
                val photo = File.createTempFile("tmp", ".png").apply { writeBytes(Base64.getDecoder().decode(base64)) }
                replyToMessageWithPhoto(photo, argsText)
                return
            } catch (e: Exception) {
                log.error(e.message)
            }
        } else
            try {
                replyToMessage(JSONObject(responseData).getString("message"))
                return
            } catch (e: Exception) {
                log.error(e.message)
            }

        replyToMessage(Strings.bad_request)
        log.error("$responseData : $args")
    }

    /* https://dreamstudio.com/api/ */

    private data class TextPromptData(
        val text: String,
        val weight: Int = 1
    )

    private data class StableDiffusionJsonData(
        val steps: Int = 10, // number of diffusion steps to run, 10 - 150
        val width: Int = 1024, // SDXL v1.0 valid dimensions are 1024x1024, 1152x896, 1216x832, 1344x768, 1536x640, 640x1536, 768x1344, 832x1216, or 896x1152
        val height: Int = 1024,
        val seed: Int = 0,
        val cfg_scale: Int = 5, // how strictly the diffusion process adheres to the prompt text (higher values keep your image closer to your prompt), 0 - 35
        val samples: Int = 1,
        val text_prompts: List<TextPromptData>
    )

    private fun sendPrompt(prompt: String) =
        "https://api.stability.ai/v1/generation/$ENGINE_ID/text-to-image".httpPost()
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $stableDiffusionKey")
            .timeout(FUEL_TIMEOUT)
            .timeoutRead(FUEL_TIMEOUT)
            .jsonBody(Gson().toJson(StableDiffusionJsonData(text_prompts = listOf(TextPromptData(prompt)))))
            .response()
}