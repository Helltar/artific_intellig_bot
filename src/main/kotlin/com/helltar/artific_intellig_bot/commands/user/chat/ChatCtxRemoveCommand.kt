package com.helltar.artific_intellig_bot.commands.user.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings

class ChatCtxRemoveCommand(ctx: MessageContext) : ChatGPTCommand(ctx) {

    override fun run() {
        if (userContextMap.containsKey(userId))
            userContextMap[userId]?.clear()

        replyToMessage(Strings.chat_context_removed)
    }
}