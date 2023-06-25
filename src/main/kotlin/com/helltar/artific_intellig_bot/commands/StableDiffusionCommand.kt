package com.helltar.artific_intellig_bot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.helltar.artific_intellig_bot.Strings
import org.slf4j.LoggerFactory
import java.io.File

class StableDiffusionCommand(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

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

        if (response.second.isSuccessful) {
            // todo: tempFile
            val photo = File.createTempFile("tmp", ".png").apply { writeBytes(response.second.data) }
            replyToMessageWithPhoto(photo, argsText)
        } else {
            replyToMessage(Strings.bad_request)
            log.error("${response.third.component2()?.message} : $args")
        }
    }

    private data class TextPromptData(
        val text: String,
        val weight: Int = 1
    )

    private data class StableDiffusionJsonData(
        val cfg_scale: Int = 7,
        val clip_guidance_preset: String = "FAST_BLUE",
        val height: Int = 512,
        val width: Int = 512,
        val samples: Int = 1,
        val steps: Int = 30,
        val text_prompts: List<TextPromptData>
    )

    private fun sendPrompt(prompt: String) =
        "https://api.stability.ai/v1alpha/generation/stable-diffusion-512-v2-0/text-to-image".httpPost()
            .header("Content-Type", "application/json")
            .header("Authorization", stableDiffusionKey)
            .header("Accept", "image/png")
            .timeout(FUEL_TIMEOUT)
            .timeoutRead(FUEL_TIMEOUT)
            .jsonBody(Gson().toJson(StableDiffusionJsonData(text_prompts = listOf(TextPromptData(prompt)))))
            .response()
}