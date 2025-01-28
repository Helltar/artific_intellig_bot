package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.helltar.aibot.Config.PROVIDER_DEEPSEEK
import com.helltar.aibot.Config.PROVIDER_OPENAI
import com.helltar.aibot.Strings
import com.helltar.aibot.Strings.localizedString
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.OpenAICommand
import com.helltar.aibot.commands.user.chat.models.Chat
import com.helltar.aibot.commands.user.chat.models.Chat.CHAT_GPT_MODEL
import com.helltar.aibot.commands.user.chat.models.Chat.DEEPSEEK_MODEL
import com.helltar.aibot.db.dao.configurationsDao
import com.helltar.aibot.utils.Network.postJson
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import java.io.ByteArrayInputStream

class Chat(ctx: MessageContext) : OpenAICommand(ctx) {

    private companion object {
        const val MAX_USER_MESSAGE_TEXT_LENGTH = 4000
        const val MAX_DIALOG_HISTORY_LENGTH = MAX_USER_MESSAGE_TEXT_LENGTH * 3
        const val VOICE_OUT_TAG = "#voice"
        val log = KotlinLogging.logger {}
    }

    private val chatHistoryManager = ChatHistoryManager(userId)

    override suspend fun run() {
        processUserMessage()
    }

    override fun getCommandName() =
        Commands.CMD_CHAT

    private suspend fun processUserMessage() {
        var messageId = ctx.messageId()
        var text = argumentsString
        var isVoiceOut = false

        if (isReply) {
            if (isNotMe(replyMessage!!.from?.userName)) {
                text =
                    replyMessage.text ?: replyMessage.caption ?: run {
                        replyToMessage(Strings.MESSAGE_TEXT_NOT_FOUND)
                        return
                    }

                isVoiceOut = argumentsString == VOICE_OUT_TAG
                messageId = replyMessage.messageId
            } else
                text = message.text
        } else
            if (argumentsString.isEmpty()) {
                replyToMessage(Strings.CHAT_HELLO)
                return
            }

        if (text.length > MAX_USER_MESSAGE_TEXT_LENGTH) {
            replyToMessage(String.format(Strings.MANY_CHARACTERS, MAX_USER_MESSAGE_TEXT_LENGTH))
            return
        }

        val username = message.from.firstName
        val chatTitle = message.chat.title ?: username

        // todo: isVoiceOut
        if (!isVoiceOut) {
            isVoiceOut = text.contains(VOICE_OUT_TAG)

            if (isVoiceOut)
                text = text.replace(VOICE_OUT_TAG, "").trim()
        }

        if (chatHistoryManager.userChatDialogHistory.isEmpty()) {
            val systemPromt = localizedString(Strings.CHAT_GPT_SYSTEM_MESSAGE, userLanguageCode)
            val systemPromtData = Chat.MessageData(Chat.CHAT_ROLE_DEVELOPER, systemPromt.format(chatTitle, username, userId))
            chatHistoryManager.addMessage(systemPromtData)
        }

        chatHistoryManager.addMessage(Chat.MessageData(Chat.CHAT_ROLE_USER, text))

        ensureDialogLengthWithinLimit()

        val answer = try {
            getBotReply()
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.CHAT_EXCEPTION)
            return
        }

        chatHistoryManager.addMessage(Chat.MessageData(Chat.CHAT_ROLE_ASSISTANT, answer))

        log.debug { "answer: $answer" }

        if (!isVoiceOut)
            replyToMessage(answer, messageId)
        else
            try {
                val voice = ByteArrayInputStream(textToSpeech(answer))
                sendVoice("voice-$messageId", voice, messageId)
            } catch (e: Exception) {
                log.error { e.message }
                replyToMessage(answer, messageId)
            }
    }

    private fun replyToMessage(answer: String, messageId: Int) {
        try {
            replyToMessage(answer, messageId, markdown = true)
        } catch (e: Exception) {
            log.error { e.message }
            errorReplyWithTextDocument(answer, Strings.TELEGRAM_API_EXCEPTION_RESPONSE_SAVED_TO_FILE)
        }
    }

    private fun ensureDialogLengthWithinLimit() {
        while (chatHistoryManager.getMessagesLengthSum() > MAX_DIALOG_HISTORY_LENGTH)
            chatHistoryManager.removeSecondMessage()
    }

    private suspend fun getBotReply(): String {
        val response = sendPrompt(chatHistoryManager.userChatDialogHistory)
        val responseJson = response.data.decodeToString()

        if (response.isSuccessful)
            return json.decodeFromString<Chat.ResponseData>(responseJson).choices.first().message.content
        else
            throw Exception("$response")
    }

    private suspend fun sendPrompt(messages: List<Chat.MessageData>): Response {
        val (url, model, provider) =
            if (!configurationsDao.isDeepSeekEnabled())
                Triple("https://api.openai.com/v1/chat/completions", CHAT_GPT_MODEL, PROVIDER_OPENAI)
            else
                Triple("https://api.deepseek.com/chat/completions", DEEPSEEK_MODEL, PROVIDER_DEEPSEEK)

        log.debug { "$url $messages" }

        val body = json.encodeToString(Chat.RequestData(model, messages))
        return postJson(url, createOpenAIHeaders(provider), body)
    }

    private suspend fun textToSpeech(input: String): ByteArray {
        val url = "https://api.openai.com/v1/audio/speech"
        val body = json.encodeToString(Chat.SpeechRequestData(input = input))
        return postJson(url, createOpenAIHeaders(), body).data
    }
}
