package com.helltar.aibot.commands.admin.ban

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class UnbanUser(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull()
            else
                ctx.message().replyToMessage?.from?.id

        if (DatabaseFactory.banListDAO.unbanUser(userId ?: return))
            replyToMessage(Strings.USER_UNBANNED)
        else
            replyToMessage(Strings.USER_NOT_BANNED)
    }

    override fun getCommandName() =
        Commands.CMD_UNBAN_USER
}