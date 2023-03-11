package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database

class AddAdminCommand(ctx: MessageContext, args: List<String> = listOf()) : BotCommand(ctx, args) {

    override fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                return

        val username = if (args.size >= 2) args[1] else ""

        if (Database.sudoers.add(userId, username))
            replyToMessage(Strings.admin_added)
        else
            replyToMessage(Strings.admin_exists)
    }
}
