package com.helltar.artific_intellig_bot.commands.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.chat.ChatGPTCommand.Companion.userContext

class ChatCtxCommand(ctx: MessageContext) : BotCommand(ctx) {

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

        if (userContext.containsKey(userId)) {
            userContext[userId]?.forEachIndexed { index, chatMessage ->
                if (chatMessage.role == "user")
                    text += "*$index*: " + chatMessage.content + "\n"
            }
        }

        if (text.isEmpty())
            text = Strings.chat_context_empty

        replyToMessage(text, markdown = true)
    }
}