package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database

class RemoveAdminCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                return

        if (isCreator(userId))
            return

        if (Database.sudoers.remove(userId))
            replyToMessage(Strings.admin_removed)
        else
            replyToMessage(Strings.admin_not_exists)
    }
}
