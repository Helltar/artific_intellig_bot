package com.helltar.aibot.commands.admin.ban

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.dao.DatabaseFactory

class BanUser(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val user = ctx.message().replyToMessage?.from ?: return
        val reason = argsText.ifEmpty { null }

        if (DatabaseFactory.banList.banUser(user, reason))
            replyToMessage(Strings.user_banned)
        else
            replyToMessage(Strings.user_already_banned)
    }
}