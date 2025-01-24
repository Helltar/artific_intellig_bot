package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.chat.models.Chat
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

class ChatCtx(ctx: MessageContext) : BotCommand(ctx) {

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
            if (userChatHistory.isNotEmpty())
                errorReplyWithTextDocument(text, Strings.TELEGRAM_API_EXCEPTION_CONTEXT_SAVED_TO_FILE)

            log.error { e.message }
        }
    }

    override fun getCommandName() =
        Commands.CMD_CHATCTX

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

    private fun formatUserChatHistory(userChatHistory: List<Chat.MessageData>) =
        if (userChatHistory.isNotEmpty()) {
            userChatHistory
                .filter { it.role == Chat.CHAT_ROLE_USER }
                .joinToString("\n") { "- ${it.content}" }
        } else
            Strings.CHAT_CONTEXT_EMPTY
}
