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
        if (isNotReply && argumentsString.isBlank()) {
            replyToMessage(Strings.CHAT_HELLO)
            return
        }

        var text: String? = argumentsString
        var messageId = message.messageId
        var botReplyWithVoice = false

        if (isReply) {
            val message = replyMessage!!

            if (isNotMyMessage(message)) {
                text = message.text ?: message.caption // photos, etc
                messageId = message.messageId
                botReplyWithVoice = argumentsString == VOICE_OUT_TAG

                if (text.isNullOrBlank()) {
                    replyToMessage(Strings.MESSAGE_TEXT_NOT_FOUND, messageId)
                    return
                }

                /*
                  ----------------------------------------------------------
                  userA: the only true wisdom is in knowing you know nothing
                  userB: /chat whose quote?
                  ----------------------------------------------------------
                */
                if (argumentsString.isNotBlank()) { // if 'userB' used the '/chat' command with args as a reply to a message from 'userA'
                    text = "$argumentsString: \"$text\"" // whose quote?: "the only true wisdom is in knowing you know nothing"
                    messageId = this.message.messageId
                }
            } else // ArtificIntelligBotHandler onUpdate
                text = this.message.text
        }

        if (text!!.length > MAX_USER_MESSAGE_TEXT_LENGTH) {
            replyToMessage(String.format(Strings.MANY_CHARACTERS, MAX_USER_MESSAGE_TEXT_LENGTH))
            return
        }

        // todo: botReplyWithVoice
        if (!botReplyWithVoice) {
            botReplyWithVoice = text.contains(VOICE_OUT_TAG)

            if (botReplyWithVoice)
                text = text.replace(VOICE_OUT_TAG, "").trim()
        }

        addSystemPromtIfNeeded()
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

        log.debug {
            chatHistoryManager.userChatDialogHistory.joinToString("\n") {
                "- ${it.message.role}: ${it.message.content}"
            }
        }

        if (!botReplyWithVoice)
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
        val messages = chatHistoryManager.userChatDialogHistory.map { it.message }

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

    private fun addSystemPromtIfNeeded() {
        if (chatHistoryManager.userChatDialogHistory.isEmpty()) {
            val systemPromt = localizedString(Strings.CHAT_GPT_SYSTEM_MESSAGE, userLanguageCode)
            val username = message.from.firstName
            val chatTitle = message.chat.title ?: username
            val systemPromtContent = systemPromt.format(chatTitle, username, userId)
            val systemPromtData = MessageData(CHAT_ROLE_SYSTEM, systemPromtContent)
            chatHistoryManager.addMessage(systemPromtData)
        }
    }

    private fun ensureDialogLengthWithinLimit() {
        while (chatHistoryManager.getMessagesLengthSum() > MAX_DIALOG_HISTORY_LENGTH)
            chatHistoryManager.removeSecondMessage()
    }
}
