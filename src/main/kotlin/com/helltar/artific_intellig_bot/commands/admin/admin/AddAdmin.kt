package com.helltar.artific_intellig_bot.commands.admin.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class AddAdmin(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                return

        val username = if (args.size >= 2) args[1] else null

        if (DatabaseFactory.sudoers.add(userId, username))
            replyToMessage(Strings.admin_added)
        else
            replyToMessage(Strings.admin_exists)
    }
}