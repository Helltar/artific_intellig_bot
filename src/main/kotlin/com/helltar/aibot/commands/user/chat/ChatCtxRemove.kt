package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands

class ChatCtxRemove(ctx: MessageContext) : ChatGPT(ctx) {

    override suspend fun run() {
        var username = ""

        val userId =
            if (!isReply)
                this.userId
            else
                if (isAdmin()) {
                    username = " (<b>${message.replyToMessage.from.firstName}</b>)"
                    message.replyToMessage.from.id
                } else {
                    replyToMessage(Strings.ADMIN_ONLY_COMMAND)
                    return
                }

        if (isCreator(userId))
            if (!isCreator()) {
                replyToMessage(Strings.CREATOR_CONTEXT_CANNOT_BE_DELETED)
                return
            }

        userChatContextMap.remove(userId)

        replyToMessage(Strings.CHAT_CONTEXT_REMOVED + username)
    }

    override fun getCommandName() =
        Commands.CMD_CHAT_CTX_REMOVE
}