package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings

class ChatCtxRemove(ctx: MessageContext) : ChatGPT(ctx) {

    override fun run() {
        var username = ""

        val userId =
            if (!message.isReply)
                this.userId
            else
                if (isAdmin()) {
                    username = " (<b>${message.replyToMessage.from.firstName}</b>)"
                    message.replyToMessage.from.id
                } else {
                    replyToMessage(Strings.creator_only_command)
                    return
                }

        userContextMap.remove(userId)

        replyToMessage(Strings.chat_context_removed + username)
    }
}