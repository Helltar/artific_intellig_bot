package com.helltar.artific_intellig_bot.commands

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.TelegramFile
import com.helltar.artific_intellig_bot.BotConfig.JSON_DALLE2
import com.helltar.artific_intellig_bot.BotConfig.OPENAI_TOKEN
import com.helltar.artific_intellig_bot.Strings
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory

class DallE2Command(bot: Bot, message: Message, args: List<String>) : BotCommand(bot, message, args) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (args.isEmpty()) {
            sendMessage(Strings.empty_args)
            return
        }

        val text = args.joinToString(" ")

        if (text.length > 1000) {
            sendMessage(String.format(Strings.many_characters, 1000))
            return
        }

        try {
            sendPhoto(
                TelegramFile.ByUrl(
                    JSONObject(sendPrompt(text).third.get())
                        .getJSONArray("data")
                        .getJSONObject(0)
                        .getString("url")
                ),
                text
            )
        } catch (e: JSONException) {
            sendMessage(Strings.chat_exception)
            log.error(e.message)
        }
    }

    private fun sendPrompt(prompt: String) =
        "https://api.openai.com/v1/images/generations".httpPost()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $OPENAI_TOKEN")
            .jsonBody(String.format(JSON_DALLE2, prompt))
            .responseString()
}
