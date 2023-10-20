package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.api.gax.rpc.ApiException
import com.google.cloud.texttospeech.v1.*
import com.google.cloud.translate.TranslateOptions
import com.google.gson.Gson
import com.helltar.aibot.BotConfig.getOpenaiApiKey
import com.helltar.aibot.Strings
import com.helltar.aibot.Strings.localizedString
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_GPT_MODEL
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_ASSISTANT
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_SYSTEM
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_USER
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.ChatData
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.ChatMessageData
import com.helltar.aibot.utils.NetworkUtils.httpPost
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

open class ChatGPT(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        val userContextMap = hashMapOf<Long, LinkedList<ChatMessageData>>()
        private const val MAX_USER_MESSAGE_TEXT_LENGTH = 512
        private const val MAX_ADMIN_MESSAGE_TEXT_LENGTH = 1024
        private const val MAX_CHAT_MODEL_CONTEXT_LENGH = 4096 // gpt-3.5-turbo
        private const val VOICE_OUT_TEXT_TAG = "#voice"
    }

    override fun run() {
        var messageId = ctx.messageId()
        var text = argsText
        var isVoiceOut = false

        /* todo: refact. */

        if (message.isReply) {
            if (message.replyToMessage.from.id != ctx.sender.me.id) {
                text = message.replyToMessage.text ?: return
                isVoiceOut = argsText == VOICE_OUT_TEXT_TAG
                messageId = message.replyToMessage.messageId
            } else
                text = message.text
        } else
            if (argsText.isEmpty()) {
                replyToMessage(Strings.CHAT_HELLO)
                return
            }

        val textLength =
            if (!isAdmin())
                MAX_USER_MESSAGE_TEXT_LENGTH
            else
                MAX_ADMIN_MESSAGE_TEXT_LENGTH

        if (text.length > textLength) {
            replyToMessage(String.format(Strings.MANY_CHARACTERS, textLength))
            return
        }

        val username = message.from.firstName
        val chatTitle = message.chat.title ?: username

        // todo: isVoiceOut
        if (!isVoiceOut) {
            isVoiceOut = text.contains(VOICE_OUT_TEXT_TAG)

            if (isVoiceOut)
                text = text.replace(VOICE_OUT_TEXT_TAG, "").trim()
        }

        val chatSystemMessage = localizedString(Strings.CHAT_GPT_SYSTEM_MESSAGE, userLanguageCode)

        if (!userContextMap.containsKey(userId))
            userContextMap[userId] =
                LinkedList(
                    listOf(
                        ChatMessageData(
                            CHAT_ROLE_SYSTEM,
                            String.format(chatSystemMessage, chatTitle, username, userId)
                        )
                    )
                )

        userContextMap[userId]?.add(ChatMessageData(CHAT_ROLE_USER, text))

        var contextLengh = getUserDialogContextLengh()

        if (contextLengh > MAX_CHAT_MODEL_CONTEXT_LENGH)
            while (contextLengh > MAX_CHAT_MODEL_CONTEXT_LENGH) {
                userContextMap[userId]?.removeAt(1) // todo: removeAt
                contextLengh = getUserDialogContextLengh()
            }

        val response = sendPrompt(userContextMap[userId]!!)
        val json = response.data.decodeToString()

        if (!response.isSuccessful) {
            try {
                replyToMessage(JSONObject(json).getJSONObject("error").getString("message"))
            } catch (e: JSONException) {
                replyToMessage(Strings.CHAT_EXCEPTION)
                log.error(e.message)
            }

            log.error("$response")
            return
        }

        val answer =
            try {
                JSONObject(json)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            } catch (e: JSONException) {
                replyToMessage(Strings.CHAT_EXCEPTION)
                log.error(e.message)
                return
            }

        userContextMap[userId]?.add(ChatMessageData(CHAT_ROLE_ASSISTANT, answer))

        if (isVoiceOut)
            sendVoice(answer, messageId)
        else
            try {
                replyToMessage(answer, messageId, markdown = true)
            } catch (e: Exception) { // todo: TelegramApiRequestException
                replyToMessageWithDocument(
                    File.createTempFile("answer", ".txt").apply { writeText(answer) },
                    Strings.TELEGRAM_API_EXCEPTION_RESPONSE_SAVED_TO_FILE
                )
                log.error(e.message)
            }
    }

    override fun getCommandName() =
        Commands.CMD_CHAT

    private fun sendVoice(text: String, messageId: Int) {
        textToSpeech(text, detectLanguage(text))?.let { oggBytes ->
            // todo: tempFile
            val voice = File.createTempFile("tmp", ".ogg").apply { writeBytes(oggBytes) }
            sendVoice(voice, messageId)
        }
            ?: replyToMessage(Strings.CHAT_EXCEPTION)
    }

    private fun textToSpeech(text: String, languageCode: String): ByteArray? {
        TextToSpeechClient.create().use { textToSpeechClient ->
            val input = SynthesisInput.newBuilder().setText(text).build()

            val language =
                if (textToSpeechClient.listVoices(languageCode).voicesList.isNotEmpty()) // todo: voicesList request
                    languageCode
                else
                    "uk"

            val voice =
                VoiceSelectionParams.newBuilder()
                    .setLanguageCode(language)
                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .build()

            val audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.OGG_OPUS).build()

            return try {
                val response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig)
                response.audioContent.toByteArray()
            } catch (e: ApiException) {
                log.error(e.message)
                null
            }
        }
    }

    private fun getUserDialogContextLengh() =
        userContextMap[userId]!!.sumOf { it.content.length }

    private fun detectLanguage(text: String) =
        TranslateOptions.getDefaultInstance().getService().detect(text).language

    private fun sendPrompt(messages: List<ChatMessageData>): Response {
        val url = "https://api.openai.com/v1/chat/completions"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer ${getOpenaiApiKey()}")
        val body = Gson().toJson(ChatData(CHAT_GPT_MODEL, messages))
        return httpPost(url, headers, body)
    }
}