package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class RemoveChatCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val chatId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                ctx.chatId()

        if (DatabaseFactory.chatWhiteList.remove(chatId))
            replyToMessage(Strings.chat_removed)
        else
            replyToMessage(Strings.chat_not_exists)
    }
}