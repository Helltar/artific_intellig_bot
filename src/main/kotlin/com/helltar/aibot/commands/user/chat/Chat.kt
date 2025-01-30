package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Config.API_KEY_PROVIDER_DEEPSEEK
import com.helltar.aibot.config.Strings
import com.helltar.aibot.config.Strings.localizedString
import com.helltar.aibot.openai.api.ApiConfig.CHAT_ROLE_ASSISTANT
import com.helltar.aibot.openai.api.ApiConfig.CHAT_ROLE_SYSTEM
import com.helltar.aibot.openai.api.ApiConfig.CHAT_ROLE_USER
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.models.common.MessageData
import com.helltar.aibot.openai.api.service.ChatService
import com.helltar.aibot.openai.api.service.DeepSeekService
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.ByteArrayInputStream

class Chat(ctx: MessageContext) : BotCommand(ctx) {

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
        Commands.User.CMD_CHAT

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
            val systemPromtData = MessageData(CHAT_ROLE_SYSTEM, systemPromtContent)
            chatHistoryManager.addMessage(systemPromtData)
        }

        chatHistoryManager.addMessage(MessageData(CHAT_ROLE_USER, text))
        ensureDialogLengthWithinLimit()

        val chatGPTService = ChatService(OpenAiClient(openaiKey()))

        val answer = try {
            getBotReply(chatGPTService)
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.CHAT_EXCEPTION)
            return
        }

        chatHistoryManager.addMessage(MessageData(CHAT_ROLE_ASSISTANT, answer))

        log.debug { "answer: $answer" }

        if (!isVoiceOut)
            replyToMessage(answer, messageId)
        else
            try {
                val voice = ByteArrayInputStream(chatGPTService.textToSpeech(answer))
                sendVoice("voice-$messageId", voice, messageId)
            } catch (e: Exception) {
                log.error { e.message }
                replyToMessage(answer, messageId)
            }
    }

    private suspend fun getBotReply(chatGPTService: ChatService): String {
        val messages = chatHistoryManager.userChatDialogHistory

        return if (isDeepSeekNotEnabled())
            chatGPTService.getReply(messages)
        else
            DeepSeekService(OpenAiClient(getApiKey(API_KEY_PROVIDER_DEEPSEEK))).getReply(messages)
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
}
