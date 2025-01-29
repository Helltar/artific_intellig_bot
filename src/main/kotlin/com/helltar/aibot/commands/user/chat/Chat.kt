package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Config.PROVIDER_DEEPSEEK
import com.helltar.aibot.Config.PROVIDER_OPENAI
import com.helltar.aibot.Strings
import com.helltar.aibot.Strings.localizedString
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.OpenAICommand
import com.helltar.aibot.commands.user.chat.models.Chat
import com.helltar.aibot.commands.user.chat.models.Chat.CHAT_API_URL
import com.helltar.aibot.commands.user.chat.models.Chat.DEEPSEEK_API_URL
import com.helltar.aibot.commands.user.chat.models.Chat.DEEPSEEK_MODEL
import com.helltar.aibot.commands.user.chat.models.Chat.GPT_MODEL
import com.helltar.aibot.commands.user.chat.models.Chat.TTS_API_URL
import com.helltar.aibot.db.dao.configurationsDao
import com.helltar.aibot.utils.Network.postAsByteArray
import com.helltar.aibot.utils.Network.postAsString
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
            val systemPromtContent = systemPromt.format(chatTitle, username, userId)
            val systemPromtData = Chat.MessageData(Chat.ROLE_DEVELOPER, systemPromtContent)
            chatHistoryManager.addMessage(systemPromtData)
        }

        chatHistoryManager.addMessage(Chat.MessageData(Chat.ROLE_USER, text))

        ensureDialogLengthWithinLimit()

        val answer = try {
            getBotReply()
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.CHAT_EXCEPTION)
            return
        }

        chatHistoryManager.addMessage(Chat.MessageData(Chat.ROLE_ASSISTANT, answer))

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
        val responseJson = sendPrompt(chatHistoryManager.userChatDialogHistory)
        return json.decodeFromString<Chat.ResponseData>(responseJson).choices.first().message.content
    }

    private suspend fun sendPrompt(messages: List<Chat.MessageData>): String {
        val (url, model, provider) = if (!configurationsDao.isDeepSeekEnabled())
            Triple(CHAT_API_URL, GPT_MODEL, PROVIDER_OPENAI)
        else
            Triple(DEEPSEEK_API_URL, DEEPSEEK_MODEL, PROVIDER_DEEPSEEK)

        log.debug { "$url $messages" }

        val body = json.encodeToString(Chat.RequestData(model, messages))
        return postAsString(url, createOpenAIHeaders(provider), body)
    }

    private suspend fun textToSpeech(input: String): ByteArray {
        val body = json.encodeToString(Chat.SpeechRequestData(input = input))
        return postAsByteArray(TTS_API_URL, createOpenAIHeaders(), body)
    }
}
