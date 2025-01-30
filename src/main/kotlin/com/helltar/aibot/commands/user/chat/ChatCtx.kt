package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.openai.api.ApiConfig.CHAT_ROLE_USER
import com.helltar.aibot.openai.api.models.common.MessageData
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

    private fun formatUserChatHistory(userChatHistory: List<MessageData>) =
        if (userChatHistory.isNotEmpty()) {
            userChatHistory
                .filter { it.role == CHAT_ROLE_USER }
                .joinToString("\n") { "- ${it.content}" }
        } else
            Strings.CHAT_CONTEXT_EMPTY
}
