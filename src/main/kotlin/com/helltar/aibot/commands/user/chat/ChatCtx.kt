package com.helltar.aibot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.user.chat.models.ChatGPTData.CHAT_ROLE_USER

class ChatCtx(ctx: MessageContext) : ChatGPT(ctx) {

    override fun run() {
        val userId =
            if (!message.isReply)
                this.userId
            else
                if (isAdmin())
                    message.replyToMessage.from.id
                else {
                    replyToMessage(Strings.CREATOR_ONLY_COMMAND)
                    return
                }

        val text =
            if (userContextMap.containsKey(userId))
                userContextMap[userId]?.filter { it.role == CHAT_ROLE_USER }?.joinToString("\n") { "- ${it.content}" }
            else
                Strings.CHAT_CONTEXT_EMPTY

        replyToMessage("$text", markdown = true)
    }
}