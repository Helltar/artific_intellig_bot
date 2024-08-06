package com.helltar.aibot.commands.admin.ban

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.banlistDao

class UnbanUser(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val userId =
            if (arguments.isNotEmpty())
                arguments[0].toLongOrNull()
            else
                ctx.message().replyToMessage?.from?.id

        if (banlistDao.unbanUser(userId ?: return))
            replyToMessage(Strings.USER_UNBANNED)
        else
            replyToMessage(Strings.USER_NOT_BANNED)
    }

    override fun getCommandName() =
        Commands.CMD_UNBAN_USER
}