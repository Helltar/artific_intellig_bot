package com.helltar.artific_intellig_bot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.helltar.artific_intellig_bot.Commands.cmdChatAsVoice
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.Utils.detectLangCode
import com.helltar.artific_intellig_bot.commands.BotCommand
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.io.File
import java.util.*

/* todo: refact. */

open class ChatGPTCommand(ctx: MessageContext, private val chatSystemMessage: String = "") : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    private data class ChatData(
        val model: String,
        val messages: List<ChatMessageData>
    )

    data class ChatMessageData(
        val role: String,
        val content: String
    )

    companion object {
        val userContext = hashMapOf<Long, LinkedList<ChatMessageData>>()
        private const val MAX_USER_MESSAGE_TEXT_LENGTH = 512
        private const val MAX_ADMIN_MESSAGE_TEXT_LENGTH = 1024
        private const val CHAT_GPT_MODEL = "gpt-3.5-turbo"
        private const val DIALOG_CONTEXT_SIZE = 15
    }

    override fun run() {
        val message = ctx.message()
        var messageId = ctx.messageId()
        var text = argsText

        // todo: temp. fix. ._.
        if (text.isEmpty())
            text = message.text

        if (!message.isReply) {
            if (args.isEmpty()) {
                replyToMessage(Strings.chat_hello)
                return
            }
        } else {
            if (message.replyToMessage.from.id != ctx.sender.me.id) {
                text = message.replyToMessage.text ?: return
                messageId = message.replyToMessage.messageId
            }
        }

        val username = message.from.firstName
        val chatTitle = message.chat.title ?: username

        val textLength =
            if (isNotAdmin())
                MAX_USER_MESSAGE_TEXT_LENGTH
            else
                MAX_ADMIN_MESSAGE_TEXT_LENGTH

        if (text.length > textLength) {
            replyToMessage(String.format(Strings.many_characters, textLength))
            return
        }

        if (userContext.containsKey(userId))
            userContext[userId]?.add(ChatMessageData("user", text))
        else
            userContext[userId] = LinkedList(listOf(ChatMessageData("user", text)))

        val waitMessageId = replyToMessageWithMarkup("...", createWaitButton())

        val json: String

        sendPrompt(username, chatTitle).run {
            if (second.isSuccessful)
                json = third.get()
            else {
                deleteMessage(waitMessageId)
                replyToMessage(Strings.chat_exception)
                log.error(third.component2()?.message)
                userContext[userId]?.removeLast()
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

            userContext[userId]?.add(ChatMessageData("assistant", answer))

            if (!File(DIR_DB + cmdChatAsVoice).exists())
                replyToMessage(answer, messageId, markdown = true)
            else {
                textToSpeech(answer, detectLangCode(answer))?.let { oggBytes ->
                    // todo: tempFile
                    val voice = File.createTempFile("tmp", ".ogg").apply { writeBytes(oggBytes) }
                    sendVoice(voice, messageId)
                }
                    ?: replyToMessage(Strings.chat_exception)
            }
        } catch (e: JSONException) {
            log.error(e.message)
        }

        deleteMessage(waitMessageId)
    }

    private fun sendPrompt(username: String, chatName: String): ResponseResultOf<String> {
        val messages = arrayListOf(ChatMessageData("system", String.format(chatSystemMessage, chatName, username, userId)))

        if (userContext[userId]!!.size > DIALOG_CONTEXT_SIZE) {
            userContext[userId]?.removeFirst()
            userContext[userId]?.removeFirst()
        }

        messages.addAll(userContext[userId]!!)

        return "https://api.openai.com/v1/chat/completions".httpPost()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $openaiKey")
            .timeout(FUEL_TIMEOUT)
            .timeoutRead(FUEL_TIMEOUT)
            .jsonBody(Gson().toJson(ChatData(CHAT_GPT_MODEL, messages)))
            .responseString()
    }

    /* https://cloud.google.com/text-to-speech/docs/reference/rest/v1/text/synthesize */

    private data class TtsInputData(
        val text: String
    )

    private data class TtsVoiceData(
        val languageCode: String,
        val ssmlGender: String = "FEMALE"
    )

    private data class TtsAudioConfigData(
        val audioEncoding: String = "OGG_OPUS",
        val speakingRate: Float = 1.0f
    )

    private data class TextToSpeechJsonData(
        val input: TtsInputData,
        val voice: TtsVoiceData,
        val audioConfig: TtsAudioConfigData
    )

    private fun textToSpeech(text: String, languageCode: String): ByteArray? {
        var json = Gson().toJson(TextToSpeechJsonData(TtsInputData(text), TtsVoiceData(languageCode), TtsAudioConfigData()))

        "https://texttospeech.googleapis.com/v1/text:synthesize?fields=audioContent&key=$googleCloudKey".httpPost()
            .header("Content-Type", "application/json; charset=utf-8")
            .jsonBody(json)
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
            Base64.getDecoder().decode(JSONObject(json).getString("audioContent"))
        } catch (e: JSONException) {
            log.error(e.message)
            null
        }
    }

    private fun createWaitButton() =
        InlineKeyboardMarkup.builder().keyboardRow(
            listOf(
                InlineKeyboardButton
                    .builder()
                    .callbackData("waitButton")
                    .text("\uD83D\uDD04") // 🔄
                    .build()
            )
        ).build()
}