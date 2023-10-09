package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.google.gson.Gson
import com.helltar.aibot.BotConfig.openaiApiKey
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.user.images.models.DalleData
import com.helltar.aibot.utils.NetworkUtils.httpPost
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory

class DallE2(ctx: MessageContext) : BotCommand(ctx) {

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

    private fun sendPrompt(prompt: String): String {
        val url = "https://api.openai.com/v1/images/generations"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer $openaiApiKey")
        val body = Gson().toJson(DalleData.RequestData(prompt, 1))
        return httpPost(url, headers, body).data.decodeToString()
    }
}