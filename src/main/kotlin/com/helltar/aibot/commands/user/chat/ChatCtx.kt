package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.chat.models.Chat
import org.slf4j.LoggerFactory

class ChatCtx(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun run() {
        val userId =
            if (!isReply) this.userId
            else {
                if (isAdmin()) message.replyToMessage.from.id
                else {
                    replyToMessage(Strings.ADMIN_ONLY_COMMAND)
                    return
                }
            }

        if (isCreator(userId))
            if (!isCreator()) {
                replyToMessage(Strings.CREATOR_CONTEXT_CANNOT_BE_VIEWED)
                return
            }

        val userChatHistory = ChatHistoryManager(userId).userChatDialogHistory

        val text =
            if (userChatHistory.isNotEmpty()) {
                userChatHistory
                    .filter { it.role == Chat.CHAT_ROLE_USER }
                    .joinToString("\n") { "- ${it.content}" }
            } else
                Strings.CHAT_CONTEXT_EMPTY

        try {
            replyToMessage(text, markdown = true)
        } catch (e: Exception) {
            errorReplyToMessageWithTextDocument(text, Strings.TELEGRAM_API_EXCEPTION_CONTEXT_SAVED_TO_FILE)
            log.error(e.message)
        }
    }

    override fun getCommandName() =
        Commands.CMD_CHATCTX
}