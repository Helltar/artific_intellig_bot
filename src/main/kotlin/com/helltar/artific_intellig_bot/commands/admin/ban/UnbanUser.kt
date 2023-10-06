package com.helltar.artific_intellig_bot.commands.admin.ban

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class UnbanUser(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull()
            else
                ctx.message().replyToMessage?.from?.id

        if (DatabaseFactory.banList.unbanUser(userId ?: return))
            replyToMessage(Strings.user_unbanned)
        else
            replyToMessage(Strings.user_not_banned)
    }
}