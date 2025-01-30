package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings

class ChatCtxRemove(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val user = message.replyToMessage?.from
        val userId = user?.id

        var username = ""

        val targetUserId =
            if (!isReply)
                this.userId
            else {
                if (isAdmin()) {
                    username = " (<b>${user?.firstName}</b>)"
                    userId ?: return
                } else {
                    replyToMessage(Strings.ADMIN_ONLY_COMMAND)
                    return
                }
            }

        if (isCreator(targetUserId)) {
            if (!isCreator()) {
                replyToMessage(Strings.CREATOR_CONTEXT_CANNOT_BE_DELETED)
                return
            }
        }

        ChatHistoryManager(targetUserId).clear()

        replyToMessage(Strings.CHAT_CONTEXT_REMOVED + username)
    }

    override fun getCommandName() =
        Commands.User.CMD_CHAT_CTX_REMOVE
}
