package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database

class AddChatCommand(ctx: MessageContext, args: List<String> = listOf()) : BotCommand(ctx, args) {

    override fun run() {
        val chatId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                ctx.chatId()

        val title = if (args.size >= 2) args[1] else ctx.message().chat.title ?: ""

        if (Database.chatWhiteList.add(chatId, title))
            replyToMessage(Strings.chat_added)
        else
            replyToMessage(Strings.chat_exists)
    }
}