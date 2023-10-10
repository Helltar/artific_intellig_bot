package com.helltar.aibot.commands.admin.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.dao.DatabaseFactory

class AddAdmin(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                return

        val username = if (args.size >= 2) args[1] else null

        if (DatabaseFactory.sudoers.add(userId, username))
            replyToMessage(Strings.ADMIN_ADDED)
        else
            replyToMessage(Strings.ADMIN_EXISTS)
    }
}