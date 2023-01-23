package com.helltar.artific_intellig_bot.commands

import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.TelegramFile
import com.helltar.artific_intellig_bot.DIR_STABLE_DIFFUSION
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.Utils
import org.slf4j.LoggerFactory
import java.io.File

class StableDiffusionCommand(bot: Bot, message: Message, args: List<String>) : BotCommand(bot, message, args) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (args.isEmpty()) {
            sendMessage(Strings.stable_diffusion_empty_args)
            return
        }

        val text = args.joinToString(" ")

        if (text.length > 2000) {
            sendMessage(String.format(Strings.many_characters, 2000))
            return
        }

        val response = sendPrompt(text)

        if (response.second.isSuccessful)
            File(DIR_STABLE_DIFFUSION + "${userId}_${Utils.randomUUID()}.png").run {
                writeBytes(response.second.data)
                sendPhoto(TelegramFile.ByFile(this), text)
            }
        else {
            sendMessage(Strings.bad_request)
            log.error("${response.third.component2()?.message} : $text")
        }
    }

    private fun sendPrompt(prompt: String) =
        sendPrompt(
            ReqData(
                "https://api.stability.ai/v1alpha/generation/stable-diffusion-512-v2-0/text-to-image",
                stableDiffusionKey, getJsonStableDiffusion(), prompt, mapOf("Accept" to "image/png")
            )
        )
}
