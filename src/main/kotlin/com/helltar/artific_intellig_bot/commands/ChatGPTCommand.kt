package com.helltar.artific_intellig_bot.commands

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpPost
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.DIR_TEXT_TO_SPEECH
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.Utils
import com.helltar.artific_intellig_bot.Utils.detectLangCode
import com.helltar.artific_intellig_bot.commands.Commands.commandChatAsText
import org.json.JSONException
import org.json.JSONObject
import org.json.simple.JSONValue
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

private val chatDialogContext = hashMapOf<Long, String>()

class ChatGPTCommand(bot: Bot, message: Message, args: List<String>) : BotCommand(bot, message, args) {

    companion object {
        private const val MAX_MESSAGE_TEXT_LENGTH = 300
        private const val MAX_DIALOG_CONTEXT_LENGTH = 256
        private const val DELIMITER = "\\n"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (args.isEmpty()) {
            sendMessage(Strings.chat_hello)
            return
        }

        val text = message.text ?: return

        if (isNotAdmin())
            if (text.length > MAX_MESSAGE_TEXT_LENGTH) {
                sendMessage(String.format(Strings.many_characters, MAX_MESSAGE_TEXT_LENGTH))
                return
            }

        if (chatDialogContext.containsKey(userId)) {
            if (chatDialogContext[userId]!!.length > MAX_DIALOG_CONTEXT_LENGTH) {
                chatDialogContext[userId] = chatDialogContext[userId]!!.substringAfter(DELIMITER)
            }

            chatDialogContext[userId] += "Human: $text$DELIMITER"
        } else
            chatDialogContext[userId] = "Human: $text$DELIMITER"

        val prompt = chatDialogContext[userId] ?: return
        val json: String

        sendPrompt(prompt).run {
            if (second.isSuccessful)
                json = third.get()
            else {
                chatDialogContext[userId] = ""
                sendMessage(Strings.chat_exception)
                log.error(third.component2()?.message)
                return
            }
        }

        try {
            val answer =
                JSONObject(json)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getString("text")
                    .substringAfter(":")

            chatDialogContext[userId] += "AI: $answer$DELIMITER"

            if (File(DIR_DB + commandChatAsText).exists())
                sendMessage(Utils.escapeHtml(answer))
            else {
                textToSpeech(answer, detectLangCode(answer))?.let {
                    sendVoice(it)
                }
                    ?: sendMessage(Strings.chat_exception)
            }
        } catch (e: JSONException) {
            log.error(e.message)
        }
    }

    private fun sendPrompt(prompt: String) =
        sendPrompt(
            ReqData(
                "https://api.openai.com/v1/completions",
                "Bearer $openaiKey", getJsonChatGPT(), prompt
            )
        )

    private fun textToSpeech(text: String, languageCode: String): ByteArray? {
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
            val audioBytes = Base64.getDecoder().decode(JSONObject(json).getString("audioContent"))
            File("$DIR_TEXT_TO_SPEECH${userId}_${Utils.randomUUID()}.ogg").writeBytes(audioBytes)
            audioBytes
        } catch (e: Exception) {
            sendMessage(Strings.chat_exception)
            log.error(e.message)
            null
        }
    }
}
