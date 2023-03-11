package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database

class RemoveChatCommand(ctx: MessageContext, args: List<String> = listOf()) : BotCommand(ctx, args) {

    override fun run() {
        val chatId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                ctx.chatId()

        if (Database.chatWhiteList.remove(chatId))
            replyToMessage(Strings.chat_removed)
        else
            replyToMessage(Strings.chat_not_exists)
    }
}