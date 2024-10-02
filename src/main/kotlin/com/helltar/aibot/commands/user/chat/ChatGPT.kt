package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.helltar.aibot.Strings
import com.helltar.aibot.Strings.localizedString
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.OpenAICommand
import com.helltar.aibot.commands.user.chat.models.Chat
import com.helltar.aibot.utils.NetworkUtils.postJson
import kotlinx.serialization.encodeToString
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream

class ChatGPT(ctx: MessageContext) : OpenAICommand(ctx) {

    private companion object {
        const val MAX_USER_MESSAGE_TEXT_LENGTH = 4000
        const val MAX_DIALOG_HISTORY_LENGTH = MAX_USER_MESSAGE_TEXT_LENGTH * 3
        const val VOICE_OUT_TAG = "#voice"
    }

    private val chatHistoryManager = ChatHistoryManager(userId)

    private val log = LoggerFactory.getLogger(javaClass)

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
            val systemPromtData = Chat.MessageData(Chat.CHAT_ROLE_SYSTEM, systemPromt.format(chatTitle, username, userId))
            chatHistoryManager.addMessage(systemPromtData)
        }

        chatHistoryManager.addMessage(Chat.MessageData(Chat.CHAT_ROLE_USER, text))

        ensureDialogLengthWithinLimit()

        getBotReply()?.let { answer ->
            chatHistoryManager.addMessage(Chat.MessageData(Chat.CHAT_ROLE_ASSISTANT, answer))

            if (!isVoiceOut) {
                try {
                    replyToMessage(answer, messageId, markdown = true)
                } catch (e: Exception) {
                    errorReplyWithTextDocument(answer, Strings.TELEGRAM_API_EXCEPTION_RESPONSE_SAVED_TO_FILE)
                    log.error(e.message)
                }
            } else
                sendVoice("voice-$messageId", ByteArrayInputStream(textToSpeech(answer)), messageId)
        }
            ?: replyToMessage(Strings.CHAT_EXCEPTION)
    }

    private fun ensureDialogLengthWithinLimit() {
        while (chatHistoryManager.getMessagesLengthSum() > MAX_DIALOG_HISTORY_LENGTH)
            chatHistoryManager.removeSecondMessage()
    }

    private suspend fun getBotReply(): String? {
        val response = sendPrompt(chatHistoryManager.userChatDialogHistory)
        val responseJson = response.data.decodeToString()

        return if (response.isSuccessful) {
            try {
                json.decodeFromString<Chat.ResponseData>(responseJson).choices.first().message.content
            } catch (e: Exception) {
                log.error(e.message)
                null
            }
        } else {
            log.error("$response")
            null
        }
    }

    private suspend fun sendPrompt(messages: List<Chat.MessageData>): Response {
        val url = "https://api.openai.com/v1/chat/completions"
        val body = json.encodeToString(Chat.RequestData(messages = messages))
        return postJson(url, createOpenAIHeaders(), body)
    }

    private suspend fun textToSpeech(input: String): ByteArray {
        val url = "https://api.openai.com/v1/audio/speech"
        val body = json.encodeToString(Chat.SpeechRequestData(input = input))
        return postJson(url, createOpenAIHeaders(), body).data
    }
}
