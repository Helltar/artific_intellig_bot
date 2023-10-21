package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.google.gson.Gson
import com.helltar.aibot.BotConfig.PROVIDER_OPENAI_COM
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.images.models.DalleData
import com.helltar.aibot.utils.NetworkUtils.httpPost
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory

class DallE2(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (args.isEmpty()) {
            replyToMessage(Strings.EMPTY_ARGS)
            return
        }

        if (argsText.length > 1000) {
            replyToMessage(String.format(Strings.MANY_CHARACTERS, 1000))
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
            replyToMessage(Strings.CHAT_EXCEPTION)
            log.error(e.message)
        }
    }

    override fun getCommandName() =
        Commands.CMD_DALLE

    private fun sendPrompt(prompt: String): String {
        val url = "https://api.openai.com/v1/images/generations"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer ${getApiKey(PROVIDER_OPENAI_COM)}")
        val body = Gson().toJson(DalleData.RequestData(prompt, 1))
        return httpPost(url, headers, body).data.decodeToString()
    }
}