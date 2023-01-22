package com.helltar.artific_intellig_bot.commands

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpPost
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.BotConfig.DIR_DB
import com.helltar.artific_intellig_bot.BotConfig.DIR_TEXT_TO_SPEECH
import com.helltar.artific_intellig_bot.BotConfig.JSON_CHATGPT
import com.helltar.artific_intellig_bot.BotConfig.JSON_TEXT_TO_SPEECH
import com.helltar.artific_intellig_bot.BotConfig.OPENAI_TOKEN
import com.helltar.artific_intellig_bot.BotConfig.TEXT_TO_SPEECH_TOKEN
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.Utils
import com.helltar.artific_intellig_bot.commands.Commands.commandChatAsText
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

private val chatDialogContext = hashMapOf<Long, String>()

class ChatGPTCommand(bot: Bot, message: Message, args: List<String>) : BotCommand(bot, message, args) {

    companion object {
        private const val MAX_MESSAGE_TEXT_LENGTH = 300
        private const val MAX_DIALOG_CONTEXT_LENGTH = 256
        private const val TEXT_TO_SPEECH_LANG_CODE = "uk-UA"
        private const val DELIMITER = "\\n"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (args.isEmpty()) {
            sendMessage(Strings.chat_hello)
            return
        }

        var text = message.text ?: return

        if (isNotAdmin())
            if (text.length > MAX_MESSAGE_TEXT_LENGTH) {
                sendMessage(String.format(Strings.many_characters, MAX_MESSAGE_TEXT_LENGTH))
                return
            }

        // todo: !
        text = text.replace("\n", " ")

        if (chatDialogContext.containsKey(userId)) {
            if (chatDialogContext[userId]!!.length > MAX_DIALOG_CONTEXT_LENGTH) {
                chatDialogContext[userId] = chatDialogContext[userId]!!.substringAfter(DELIMITER)
            }

            chatDialogContext[userId] += "Human: $text$DELIMITER"
        } else
            chatDialogContext[userId] = "Human: $text$DELIMITER"

        var prompt = chatDialogContext[userId] ?: return

        // todo: !
        prompt = prompt.replace("""[^\p{L}\p{Z}\d,.:]""".toRegex(), "")

        val response = sendPrompt(prompt)

        response.second.run {
            if (!isSuccessful) {
                log.error("$responseMessage $statusCode")
                sendMessage(Strings.chat_exception)
                chatDialogContext[userId] = ""
                return
            }
        }

        try {
            val answer =
                JSONObject(response.third.get())
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getString("text")

                    // todo: !
                    .replace("'", "")
                    .replace("\"", "")
                    .replace("\\", "")

                    .substringAfter(":")

            chatDialogContext[userId] += "AI: $answer$DELIMITER"

            if (File(DIR_DB + commandChatAsText).exists())
                sendMessage(answer)
            else
                sendVoice(textToSpeech(Utils.escapeHtml(answer), TEXT_TO_SPEECH_LANG_CODE) ?: return)

        } catch (e: JSONException) {
            log.error(e.message)
        }
    }

    private fun sendPrompt(prompt: String) =
        "https://api.openai.com/v1/completions".httpPost()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $OPENAI_TOKEN")
            .jsonBody(String.format(JSON_CHATGPT, prompt))
            .responseString()

    private fun textToSpeech(text: String, languageCode: String): ByteArray? {
        val json =
            "https://texttospeech.googleapis.com/v1/text:synthesize?fields=audioContent&key=$TEXT_TO_SPEECH_TOKEN"
                .httpPost()
                .header("Content-Type", "application/json; charset=utf-8")
                .jsonBody(String.format(JSON_TEXT_TO_SPEECH, text, languageCode))
                .responseString()
                .third.get()

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
