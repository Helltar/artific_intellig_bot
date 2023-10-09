package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.api.gax.rpc.ApiException
import com.google.cloud.texttospeech.v1.*
import com.google.cloud.translate.TranslateOptions
import com.google.gson.Gson
import com.helltar.aibot.BotConfig.DIR_FILES
import com.helltar.aibot.BotConfig.FILE_LOADING_GIF
import com.helltar.aibot.BotConfig.openaiApiKey
import com.helltar.aibot.Strings
import com.helltar.aibot.Strings.localizedString
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands.cmdChatAsVoice
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_GPT_MODEL
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_ASSISTANT
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_SYSTEM
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_USER
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.ChatData
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.ChatMessageData
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.utils.NetworkUtils.httpPost
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import java.io.File
import java.util.*

open class ChatGPT(ctx: MessageContext) : BotCommand(ctx) {

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
        var isVoiceOut = false

        if (message.isReply) {
            if (message.replyToMessage.from.id != ctx.sender.me.id) {
                text = message.replyToMessage.text ?: return
                isVoiceOut = argsText == VOICE_OUT_TEXT_TAG
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

        // todo: isVoiceOut
        if (!isVoiceOut) {
            isVoiceOut = text.contains(VOICE_OUT_TEXT_TAG)

            if (isVoiceOut)
                text = text.replace(VOICE_OUT_TEXT_TAG, "").trim()
        }

        val userLanguageCode = ctx.user().languageCode ?: DEFAULT_LANG_CODE
        val chatSystemMessage = localizedString(Strings.chat_gpt_system_message, userLanguageCode)

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

        // todo: removeAt
        if (userContextMap[userId]!!.size > DIALOG_CONTEXT_SIZE) {
            userContextMap[userId]?.removeAt(1) // user
            userContextMap[userId]?.removeAt(1) // assistant
        }

        val waitMessageId =
            replyToMessageWithDocument(
                getLoadingGifFileId(),
                localizedString(Strings.chat_wait_message, userLanguageCode)
            )

        val response = sendPrompt(userContextMap[userId]!!)
        val json: String

        if (response.isSuccessful)
            json = response.data.decodeToString()
        else {
            deleteMessage(waitMessageId)
            replyToMessage(Strings.chat_exception)

            userContextMap.remove(userId)
            replyToMessage(Strings.chat_context_removed_info)

            log.error("$response")

            return
        }

        try {
            val answer =
                JSONObject(json)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

//            val usageTotalTokens =
//                JSONObject(json)
//                    .getJSONObject("usage")
//                    .getInt("total_tokens")

            userContextMap[userId]?.add(ChatMessageData(CHAT_ROLE_ASSISTANT, answer))

            if (isVoiceOut)
                sendVoice(answer, messageId)
            else
                if (isCommandDisabled(cmdChatAsVoice)) {
                    try {
                        replyToMessage(answer, messageId, markdown = true)
                    } catch (e: TelegramApiRequestException) { // todo: TelegramApiRequestException
                        replyToMessageWithDocument(
                            File.createTempFile("answer", ".txt").apply { writeText(answer) },
                            "TelegramApiRequestException, response saved to file"
                        )
                        log.error(e.message)
                    }
                } else
                    sendVoice(answer, messageId)

            deleteMessage(waitMessageId)

        } catch (e: JSONException) {
            log.error(e.message)
            replyToMessage(Strings.chat_exception)
        }
    }

    private fun sendVoice(text: String, messageId: Int) {
        textToSpeech(text, detectLanguage(text))?.let { oggBytes ->
            // todo: tempFile
            val voice = File.createTempFile("tmp", ".ogg").apply { writeBytes(oggBytes) }
            sendVoice(voice, messageId)
        }
            ?: replyToMessage(Strings.chat_exception)
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

    private fun detectLanguage(text: String) =
        TranslateOptions.getDefaultInstance().getService().detect(text).language

    // todo: getLoadingGifFileId
    private fun getLoadingGifFileId() =
        DatabaseFactory.filesIds.getFileId(FILE_LOADING_GIF)
            ?: run {
                val message = sendDocument(File("$DIR_FILES/$FILE_LOADING_GIF"))
                val fileId = message.document.fileId
                deleteMessage(message.messageId)

                DatabaseFactory.filesIds.add(FILE_LOADING_GIF, fileId)

                return fileId
            }

    private fun sendPrompt(messages: List<ChatMessageData>): Response {
        val url = "https://api.openai.com/v1/chat/completions"
        val headers = mapOf("Content-Type" to "application/json", "Authorization" to "Bearer $openaiApiKey")
        val body = Gson().toJson(ChatData(CHAT_GPT_MODEL, messages))
        return httpPost(url, headers, body)
    }
}