package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.commands.user.chat.ChatHistoryManager.ChatMessage
import com.helltar.aibot.config.Strings
import com.helltar.aibot.openai.api.ApiConfig.CHAT_ROLE_USER
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.format.DateTimeFormatter

class ChatCtx(ctx: MessageContext) : BotCommand(ctx) {

    private companion object {
        val log = KotlinLogging.logger {}
    }

    override suspend fun run() {
        val userId = getUserId() ?: return

        if (isCreator(userId) && !isCreator()) {
            replyToMessage(Strings.CREATOR_CONTEXT_CANNOT_BE_VIEWED)
            return
        }

        val userChatHistory = ChatHistoryManager(userId).userChatDialogHistory
        val text = formatUserChatHistory(userChatHistory)

        try {
            replyToMessage(text, markdown = true)
        } catch (e: Exception) {
            log.error { e.message }

            if (userChatHistory.isNotEmpty())
                errorReplyWithTextDocument(text, Strings.TELEGRAM_API_EXCEPTION_CONTEXT_SAVED_TO_FILE)
        }
    }

    override fun getCommandName() =
        Commands.User.CMD_CHATCTX

    private suspend fun getUserId() =
        if (!isReply)
            this.userId
        else {
            if (isAdmin())
                message.replyToMessage.from.id
            else {
                replyToMessage(Strings.ADMIN_ONLY_COMMAND)
                null
            }
        }

    private fun formatUserChatHistory(userChatHistory: List<ChatMessage>) =
        if (userChatHistory.isNotEmpty()) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")

            userChatHistory
                .filter { it.message.role == CHAT_ROLE_USER }
                .joinToString("\n") { "▫\uFE0F *${it.datetime.format(formatter)}* - ${it.message.content}" } // ▫️
        } else
            Strings.CHAT_CONTEXT_EMPTY
}
