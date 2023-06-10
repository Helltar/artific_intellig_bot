package com.helltar.artific_intellig_bot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.Utils.detectLangCode
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsVoice
import kotlinx.serialization.Serializable
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Message
import java.io.File
import java.util.*

/* todo: refact. */

@Serializable
private data class Chat(val model: String, val messages: List<ChatMessage>)

@Serializable
private data class ChatMessage(val role: String, val content: String)

private val userContext = hashMapOf<Long, LinkedList<ChatMessage>>()

class ChatGPTCommand(ctx: MessageContext, private val chatSystemMessage: String) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    private companion object {
        const val MAX_USER_MESSAGE_TEXT_LENGTH = 300
        const val MAX_ADMIN_MESSAGE_TEXT_LENGTH = 1024
        const val CHAT_GPT_MODEL = "gpt-3.5-turbo"
        const val DIALOG_CONTEXT_SIZE = 15
    }

    override fun run() {
        val message = ctx.message()
        val isReply = message.isReply
        var messageId = ctx.messageId()
        var text = argsText

        // todo: temp. fix. ._.
        if (text.isEmpty())
            text = message.text

        if (!isReply) {
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

        if (args.isNotEmpty()) {
            when (args[0]) {
                "rm" -> {
                    clearUserContext()
                    return
                }

                "ctx" -> {
                    printUserContext(message, isReply)
                    return
                }
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
            userContext[userId]?.add(ChatMessage("user", text))
        else
            userContext[userId] = LinkedList(listOf(ChatMessage("user", text)))

        val json: String

        sendPrompt(username, chatTitle).run {
            if (second.isSuccessful)
                json = third.get()
            else {
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

            userContext[userId]?.add(ChatMessage("assistant", answer))

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
    }

    private fun clearUserContext() {
        if (userContext.containsKey(userId))
            userContext[userId]?.clear()

        replyToMessage(Strings.context_removed)
    }

    private fun printUserContext(message: Message, isReply: Boolean) {
        val userId =
            if (!isReply)
                this.userId
            else
                if (isAdmin())
                    message.replyToMessage.from.id
                else
                    return

        var text = ""

        if (userContext.containsKey(userId)) {
            userContext[userId]?.forEachIndexed { index, chatMessage ->
                if (chatMessage.role == "user")
                    text += "*$index*: " + chatMessage.content + "\n"
            }
        }

        if (text.isEmpty())
            text = "▫\uFE0F Empty"

        replyToMessage(text, markdown = true)
    }

    private fun sendPrompt(username: String, chatName: String): ResponseResultOf<String> {
        val messages = arrayListOf(ChatMessage("system", String.format(chatSystemMessage, chatName, username, userId)))

        if (userContext[userId]!!.size > DIALOG_CONTEXT_SIZE) {
            userContext[userId]?.removeFirst()
            userContext[userId]?.removeFirst()
        }

        messages.addAll(userContext[userId]!!)

        return "https://api.openai.com/v1/chat/completions".httpPost()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $openaiKey")
            .jsonBody(Gson().toJson(Chat(CHAT_GPT_MODEL, messages)))
            .responseString()
    }

    /* https://cloud.google.com/text-to-speech/docs/reference/rest/v1/text/synthesize */

    private data class InputData(
        val text: String
    )

    private data class VoiceData(
        val languageCode: String,
        val ssmlGender: String = "FEMALE"
    )

    private data class AudioConfigData(
        val audioEncoding: String = "OGG_OPUS",
        val speakingRate: Float = 1.0f
    )

    private data class TextToSpeechJsonData(
        val input: InputData,
        val voice: VoiceData,
        val audioConfig: AudioConfigData
    )

    private fun textToSpeech(text: String, languageCode: String): ByteArray? {
        var json = Gson().toJson(TextToSpeechJsonData(InputData(text), VoiceData(languageCode), AudioConfigData()))

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
}