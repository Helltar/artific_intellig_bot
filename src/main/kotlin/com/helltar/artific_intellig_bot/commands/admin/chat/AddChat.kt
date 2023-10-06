package com.helltar.artific_intellig_bot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class AddChat(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val chatId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                ctx.chatId()

        val title = if (args.size >= 2) args[1] else ctx.message().chat.title

        if (DatabaseFactory.chatWhiteList.add(chatId, title))
            replyToMessage(Strings.chat_added)
        else
            replyToMessage(Strings.chat_exists)
    }
}