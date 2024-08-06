package com.helltar.aibot.commands.admin.ban

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.banlistDao

class BanUser(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val user = ctx.message().replyToMessage?.from ?: return
        val reason = argumentsString.ifEmpty { null }

        if (banlistDao.banUser(user, reason))
            replyToMessage(Strings.USER_BANNED)
        else
            replyToMessage(Strings.USER_ALREADY_BANNED)
    }

    override fun getCommandName() =
        Commands.CMD_BAN_USER
}