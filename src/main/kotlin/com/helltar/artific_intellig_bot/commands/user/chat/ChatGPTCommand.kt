package com.helltar.artific_intellig_bot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.helltar.artific_intellig_bot.BotMainConfig
import com.helltar.artific_intellig_bot.Commands.cmdChatAsVoice
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.CHAT_GPT_MODEL
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.CHAT_ROLE_ASSISTANT
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.CHAT_ROLE_SYSTEM
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.CHAT_ROLE_USER
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.ChatData
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.ChatMessageData
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.TextToSpeechJsonData
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.TtsAudioConfigData
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.TtsInputData
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.TtsVoiceData
import com.helltar.artific_intellig_bot.localizedString
import com.helltar.artific_intellig_bot.utils.Utils.detectLangCode
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

open class ChatGPTCommand(ctx: MessageContext, private val botConfig: BotMainConfig) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        val userContextMap = hashMapOf<Long, LinkedList<ChatMessageData>>()
        private const val MAX_USER_MESSAGE_TEXT_LENGTH = 512
        private const val MAX_ADMIN_MESSAGE_TEXT_LENGTH = 1024
        private const val DIALOG_CONTEXT_SIZE = 25
        private const val VOICE_OUT_TEXT_TAG = "#voice"
        private const val DEFAULT_LANG_CODE = "en"
    }

    override fun run() {
        var messageId = ctx.messageId()
        var text = argsText

        if (message.isReply) {
            if (message.replyToMessage.from.id != ctx.sender.me.id) {
                text = message.replyToMessage.text ?: return
                messageId = message.replyToMessage.messageId
            } else
                text = message.text
        } else
            if (argsText.isEmpty()) {
                replyToMessage(Strings.chat_hello)
                return
            }

        val textLength =
            if (isNotAdmin())
                MAX_USER_MESSAGE_TEXT_LENGTH
            else
                MAX_ADMIN_MESSAGE_TEXT_LENGTH

        if (text.length > textLength) {
            replyToMessage(String.format(Strings.many_characters, textLength))
            return
        }

        val username = message.from.firstName
        val chatTitle = message.chat.title ?: username
        val isVoiceOut = text.contains(VOICE_OUT_TEXT_TAG)

        if (isVoiceOut)
            text = text.replace(VOICE_OUT_TEXT_TAG, "").trim()

        val userLanguageCode = ctx.user().languageCode ?: DEFAULT_LANG_CODE
        val chatSystemMessage = localizedString(Strings.chat_gpt_system_message, userLanguageCode)

        if (!userContextMap.containsKey(userId))
            userContextMap[userId] =
                LinkedList(listOf(ChatMessageData(CHAT_ROLE_SYSTEM, String.format(chatSystemMessage, chatTitle, username, userId))))

        userContextMap[userId]?.add(ChatMessageData(CHAT_ROLE_USER, text))

        if (userContextMap[userId]!!.size > DIALOG_CONTEXT_SIZE) {
            userContextMap[userId]?.removeAt(1) // user
            userContextMap[userId]?.removeAt(1) // assistant
        }

        val waitMessageId =
            replyToMessageWithDocument(
                botConfig.fileIdGifChatLoading,
                localizedString(Strings.chat_wait_message, userLanguageCode)
            )

        val (_, response, resultString) = sendPrompt(userContextMap[userId]!!)
        val json: String

        if (response.isSuccessful)
            json = resultString.get()
        else {
            deleteMessage(waitMessageId)
            replyToMessage(Strings.chat_exception)
            log.error(resultString.component2()?.message)
            userContextMap[userId]?.removeLast()
            return
        }

        try {
            val answer =
                JSONObject(json)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

            userContextMap[userId]?.add(ChatMessageData(CHAT_ROLE_ASSISTANT, answer))

            deleteMessage(waitMessageId)

            if (isVoiceOut) {
                sendVoice(answer, messageId)
                return
            }

            if (!File(DIR_DB + cmdChatAsVoice).exists())
                replyToMessage(answer, messageId, markdown = true)
            else
                sendVoice(answer, messageId)

        } catch (e: JSONException) {
            log.error(e.message)
            replyToMessage(Strings.chat_exception)
        }
    }

    private fun sendVoice(text: String, messageId: Int) {
        textToSpeech(text, detectLangCode(text))?.let { oggBytes ->
            // todo: tempFile
            val voice = File.createTempFile("tmp", ".ogg").apply { writeBytes(oggBytes) }
            sendVoice(voice, messageId)
        }
            ?: replyToMessage(Strings.chat_exception)
    }

    private fun sendPrompt(messages: List<ChatMessageData>) =
        "https://api.openai.com/v1/chat/completions".httpPost()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $openaiKey")
            .timeout(FUEL_TIMEOUT)
            .timeoutRead(FUEL_TIMEOUT)
            .jsonBody(Gson().toJson(ChatData(CHAT_GPT_MODEL, messages)))
            .responseString()

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
}