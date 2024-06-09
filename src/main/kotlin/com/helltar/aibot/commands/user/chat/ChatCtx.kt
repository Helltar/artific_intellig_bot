package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_USER
import org.slf4j.LoggerFactory
import java.io.File

class ChatCtx(ctx: MessageContext) : ChatGPT(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun run() {
        val userId =
            if (!isReply)
                this.userId
            else
                if (isAdmin())
                    message.replyToMessage.from.id
                else {
                    replyToMessage(Strings.ADMIN_ONLY_COMMAND)
                    return
                }

        if (isCreator(userId))
            if (!isCreator()) {
                replyToMessage(Strings.CREATOR_CONTEXT_CANNOT_BE_VIEWED)
                return
            }

        val text =
            if (userContextMap.containsKey(userId))
                userContextMap[userId]?.filter { it.role == CHAT_ROLE_USER }?.joinToString("\n") { "- ${it.content}" }
            else
                Strings.CHAT_CONTEXT_EMPTY

        try {
            replyToMessage("$text", markdown = true)
        } catch (e: Exception) { // todo: TelegramApiRequestException
            text?.let {
                replyToMessageWithDocument(
                    File.createTempFile("context", ".txt").apply { writeText(it) },
                    Strings.TELEGRAM_API_EXCEPTION_CONTEXT_SAVED_TO_FILE
                )
            }

            log.error(e.message)
        }

    }

    override fun getCommandName() =
        Commands.CMD_CHATCTX
}