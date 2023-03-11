package com.helltar.artific_intellig_bot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory

class DallE2Command(ctx: MessageContext, args: List<String> = listOf()) : BotCommand(ctx, args) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (args.isEmpty()) {
            replyToMessage(Strings.empty_args)
            return
        }

        val text = args.joinToString(" ")

        if (text.length > 1000) {
            replyToMessage(String.format(Strings.many_characters, 1000))
            return
        }

        try {
            replyToMessageWithPhoto(
                JSONObject(sendPrompt(text).third.get())
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getString("url"),
                text
            )
        } catch (e: JSONException) {
            replyToMessage(Strings.chat_exception)
            log.error(e.message)
        }
    }

    private fun sendPrompt(prompt: String) =
        sendPrompt(
            ReqData(
                "https://api.openai.com/v1/images/generations",
                "Bearer $openaiKey", getJsonDalle2(), prompt
            )
        )
}
