package com.helltar.aibot.commands.admin.ban

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.banlistDao

class UnbanUser(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val userId =
            if (arguments.isNotEmpty())
                arguments[0].toLongOrNull()
            else
                ctx.message().replyToMessage?.from?.id

        if (banlistDao.unban(userId ?: return))
            replyToMessage(Strings.USER_UNBANNED)
        else
            replyToMessage(Strings.USER_NOT_BANNED)
    }

    override fun getCommandName() =
        Commands.Admin.CMD_UNBAN_USER
}
