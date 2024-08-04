package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.google.gson.Gson
import com.helltar.aibot.Strings
import com.helltar.aibot.Strings.localizedString
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.chat.models.ChatGPTData
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_GPT_MODEL_4
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_GPT_MODEL_4_MINI
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_ASSISTANT
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_SYSTEM
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_USER
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.ChatData
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.ChatMessageData
import com.helltar.aibot.dao.DatabaseFactory.PROVIDER_OPENAI_COM
import com.helltar.aibot.utils.NetworkUtils.httpPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

open class ChatGPT(ctx: MessageContext) : BotCommand(ctx) {

    companion object {
        val userChatContextMap = hashMapOf<Long, LinkedList<ChatMessageData>>()
        private const val MAX_USER_MESSAGE_TEXT_LENGTH = 2048
        private const val MAX_ADMIN_MESSAGE_TEXT_LENGTH = 4096
        private const val MAX_CHAT_CONTEXT_LENGH = 8192
        private const val VOICE_OUT_TEXT_TAG = "#voice"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun run() {
        var messageId = ctx.messageId()
        var text = argsText
        var isVoiceOut = false

        if (isReply) {
            if (isNotMe(replyMessage!!.from?.userName)) {
                text =
                    replyMessage.text ?: replyMessage.caption ?: run {
                        replyToMessage(Strings.MESSAGE_TEXT_NOT_FOUND)
                        return
                    }

                isVoiceOut = argsText == VOICE_OUT_TEXT_TAG
                messageId = replyMessage.messageId
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

        if (!userChatContextMap.containsKey(userId))
            userChatContextMap[userId] =
                LinkedList(listOf(ChatMessageData(CHAT_ROLE_SYSTEM, String.format(chatSystemMessage, chatTitle, username, userId))))

        userChatContextMap[userId]?.add(ChatMessageData(CHAT_ROLE_USER, text))

        var contextLengh = getUserDialogContextLengh()

        if (contextLengh > MAX_CHAT_CONTEXT_LENGH)
            while (contextLengh > MAX_CHAT_CONTEXT_LENGH) {
                userChatContextMap[userId]?.removeAt(1) // todo: removeAt
                contextLengh = getUserDialogContextLengh()
            }

        val gptModel = if (!isAdmin()) CHAT_GPT_MODEL_4_MINI else CHAT_GPT_MODEL_4
        val response = sendPrompt(userChatContextMap[userId]!!, gptModel)
        val json = response.data.decodeToString()

        if (!response.isSuccessful) {
            if (response.statusCode != 401) { // Unauthorized
                try {
                    replyToMessage(JSONObject(json).getJSONObject("error").getString("message"))
                } catch (e: JSONException) {
                    replyToMessage(Strings.CHAT_EXCEPTION)
                    log.error(e.message)
                }
            } else
                replyToMessage(Strings.CHAT_UNAUTHORIZED)

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

        userChatContextMap[userId]?.add(ChatMessageData(CHAT_ROLE_ASSISTANT, answer))

        if (!isVoiceOut) {
            try {
                replyToMessage(answer, messageId, markdown = true)
            } catch (e: Exception) { // todo: TelegramApiRequestException
                replyToMessageWithDocument(
                    withContext(Dispatchers.IO) { File.createTempFile("answer", ".txt") }.apply { writeText(answer) },
                    Strings.TELEGRAM_API_EXCEPTION_RESPONSE_SAVED_TO_FILE
                )

                log.error(e.message)
            }
        } else
            sendVoice(textToSpeech(answer), messageId)
    }

    override fun getCommandName() =
        Commands.CMD_CHAT

    protected suspend fun getOpenAIAuthorizationHeader() =
        mapOf("Authorization" to "Bearer ${getApiKey(PROVIDER_OPENAI_COM)}")

    protected suspend fun getOpenAIHeaders() =
        mapOf("Content-Type" to "application/json") + getOpenAIAuthorizationHeader()

    private fun getUserDialogContextLengh() =
        userChatContextMap[userId]!!.sumOf { it.content.length }

    private suspend fun sendPrompt(messages: List<ChatMessageData>, gptModel: String): Response {
        val url = "https://api.openai.com/v1/chat/completions"
        val body = Gson().toJson(ChatData(gptModel, messages))
        return httpPost(url, getOpenAIHeaders(), body)
    }

    private suspend fun textToSpeech(input: String): File {
        val url = "https://api.openai.com/v1/audio/speech"
        val body = Gson().toJson(ChatGPTData.SpeechData(input = input))
        val data = httpPost(url, getOpenAIHeaders(), body).data
        return withContext(Dispatchers.IO) { File.createTempFile("tmp", ".ogg") }.apply { writeBytes(data) }
    }
}