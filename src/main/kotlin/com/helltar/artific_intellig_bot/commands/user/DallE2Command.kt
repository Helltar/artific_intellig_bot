package com.helltar.artific_intellig_bot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory

class DallE2Command(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (args.isEmpty()) {
            replyToMessage(Strings.empty_args)
            return
        }

        if (argsText.length > 1000) {
            replyToMessage(String.format(Strings.many_characters, 1000))
            return
        }

        try {
            replyToMessageWithPhoto(
                JSONObject(sendPrompt(argsText))
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getString("url"),
                argsText
            )
        } catch (e: JSONException) {
            replyToMessage(Strings.chat_exception)
            log.error(e.message)
        }
    }

    /* https://beta.openai.com/docs/guides/images/usage?lang=curl */

    private data class DalleJsonData(
        val prompt: String,
        val n: Int,
        val size: String = "256x256"
    )

    private fun sendPrompt(prompt: String) =
        "https://api.openai.com/v1/images/generations".httpPost()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $openaiKey")
            .timeout(60000)
            .timeoutRead(60000)
            .jsonBody(Gson().toJson(DalleJsonData(prompt, 1)))
            .response().second.data.decodeToString()
}