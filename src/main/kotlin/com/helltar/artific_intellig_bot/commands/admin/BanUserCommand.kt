package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class BanUserCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val user = ctx.message().replyToMessage?.from ?: return
        val reason = argsText.ifEmpty { null }

        if (DatabaseFactory.banList.banUser(user, reason))
            replyToMessage(Strings.user_banned)
        else
            replyToMessage(Strings.user_already_banned)
    }
}