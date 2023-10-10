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
                    replyToMessage(Strings.CREATOR_ONLY_COMMAND)
                    return
                }

        userContextMap.remove(userId)

        replyToMessage(Strings.CHAT_CONTEXT_REMOVED + username)
    }
}