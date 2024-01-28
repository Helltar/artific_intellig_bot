package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_USER

class ChatCtx(ctx: MessageContext) : ChatGPT(ctx) {

    override fun run() {
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

        replyToMessage("$text", markdown = true)
    }

    override fun getCommandName() =
        Commands.CMD_CHATCTX
}