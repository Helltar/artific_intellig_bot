package com.helltar.aibot.commands.admin.sudoers

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.sudoersDao

class RemoveAdmin(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val userId =
            if (arguments.isNotEmpty())
                arguments[0].toLongOrNull() ?: return
            else
                return

        if (isCreator(userId))
            return

        if (sudoersDao.remove(userId))
            replyToMessage(Strings.ADMIN_REMOVED)
        else
            replyToMessage(Strings.ADMIN_NOT_EXISTS)
    }

    override fun getCommandName() =
        Commands.Admin.CMD_RM_ADMIN
}
