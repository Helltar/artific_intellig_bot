package com.helltar.artific_intellig_bot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTData.CHAT_ROLE_USER

class ChatCtxCommand(ctx: MessageContext) : ChatGPTCommand(ctx) {

    override fun run() {
        val message = ctx.message()

        val userId =
            if (!message.isReply)
                this.userId
            else
                if (isAdmin())
                    message.replyToMessage.from.id
                else
                    return

        var text = ""

        if (userContextMap.containsKey(userId))
            userContextMap[userId]?.filter { it.role == CHAT_ROLE_USER }?.forEachIndexed { index, chatMessage ->
                text += "*$index*: ${chatMessage.content}\n"
            }

        if (text.isEmpty())
            text = Strings.chat_context_empty

        replyToMessage(text, markdown = true)
    }
}