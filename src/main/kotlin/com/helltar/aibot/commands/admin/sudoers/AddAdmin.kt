package com.helltar.aibot.commands.admin.sudoers

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.sudoersDao

class AddAdmin(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val userId =
            if (arguments.isNotEmpty())
                arguments[0].toLongOrNull() ?: return
            else
                return

        val username = if (arguments.size >= 2) arguments[1] else null

        if (sudoersDao.add(userId, username))
            replyToMessage(Strings.ADMIN_ADDED)
        else
            replyToMessage(Strings.ADMIN_EXISTS)
    }

    override fun getCommandName() =
        Commands.Creator.CMD_ADD_ADMIN
}
