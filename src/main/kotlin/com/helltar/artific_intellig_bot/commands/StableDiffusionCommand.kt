package com.helltar.artific_intellig_bot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.isSuccessful
import com.helltar.artific_intellig_bot.DIR_OUT_STABLE_DIFFUSION
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.Utils
import org.slf4j.LoggerFactory
import java.io.File

class StableDiffusionCommand(ctx: MessageContext, args: List<String> = listOf()) : BotCommand(ctx, args) {

    override fun run() {
        if (args.isEmpty()) {
            replyToMessage(Strings.stable_diffusion_empty_args)
            return
        }

        val text = args.joinToString(" ")

        if (text.length > 2000) {
            replyToMessage(String.format(Strings.many_characters, 2000))
            return
        }

        val response = sendPrompt(text)

        if (response.second.isSuccessful)
            File(DIR_OUT_STABLE_DIFFUSION + "${userId}_${Utils.randomUUID()}.png").run {
                writeBytes(response.second.data)
                replyToMessageWithPhoto(this, text)
                this.delete()
            }
        else {
            replyToMessage(Strings.bad_request)
            LoggerFactory.getLogger(javaClass).error("${response.third.component2()?.message} : $text")
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
