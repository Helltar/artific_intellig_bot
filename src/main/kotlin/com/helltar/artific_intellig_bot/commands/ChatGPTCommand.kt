package com.helltar.artific_intellig_bot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpPost
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.DIR_OUT_TEXT_TO_SPEECH
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.Utils
import com.helltar.artific_intellig_bot.Utils.detectLangCode
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsVoice
import org.json.JSONException
import org.json.JSONObject
import org.json.simple.JSONValue
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

class ChatGPTCommand(ctx: MessageContext, args: List<String> = listOf()) : BotCommand(ctx, args) {

    companion object {
        private const val MAX_MESSAGE_TEXT_LENGTH = 300
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (args.isEmpty()) {
            replyToMessage(Strings.chat_hello)
            return
        }

        val text = ctx.message().text ?: return

        val textLength =
            if (isNotAdmin())
                MAX_MESSAGE_TEXT_LENGTH
            else
                1024

        if (text.length > textLength) {
            replyToMessage(String.format(Strings.many_characters, textLength))
            return
        }

        val json: String

        sendPrompt(text).run {
            if (second.isSuccessful)
                json = third.get()
            else {
                replyToMessage(Strings.chat_exception)
                log.error(third.component2()?.message)
                return
            }
        }

        try {
            val answer =
                JSONObject(json)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

            if (!File(DIR_DB + cmdChatAsVoice).exists())
                replyToMessage(answer, markdown = true)
            else {
                textToSpeech(answer, detectLangCode(answer))?.let {
                    sendVoice(it)
                    it.delete()
                }
                    ?: replyToMessage(Strings.chat_exception)
            }
        } catch (e: JSONException) {
            log.error(e.message)
        }
    }

    private fun sendPrompt(prompt: String) =
        sendPrompt(
            ReqData(
                "https://api.openai.com/v1/chat/completions",
                "Bearer $openaiKey", getJsonChatGPT(), prompt
            )
        )

    private fun textToSpeech(text: String, languageCode: String): File? {
        var json: String

        "https://texttospeech.googleapis.com/v1/text:synthesize?fields=audioContent&key=$googleCloudKey"
            .httpPost()
            .header("Content-Type", "application/json; charset=utf-8")
            .jsonBody(String.format(getJsonTextToSpeech(), JSONValue.escape(text), languageCode))
            .responseString().run {
                json =
                    if (second.isSuccessful)
                        third.get()
                    else {
                        log.error(this.third.component2()?.message)
                        return null
                    }
            }

        return try {
            File("$DIR_OUT_TEXT_TO_SPEECH${userId}_${Utils.randomUUID()}.ogg").run {
                writeBytes(Base64.getDecoder().decode(JSONObject(json).getString("audioContent")))
                this
            }
        } catch (e: Exception) {
            replyToMessage(Strings.chat_exception)
            log.error(e.message)
            null
        }
    }
}
