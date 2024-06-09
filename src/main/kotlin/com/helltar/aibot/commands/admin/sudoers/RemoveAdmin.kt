package com.helltar.aibot.commands.admin.sudoers

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class RemoveAdmin(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                return

        if (isCreator(userId))
            return

        if (DatabaseFactory.sudoersDAO.remove(userId))
            replyToMessage(Strings.ADMIN_REMOVED)
        else
            replyToMessage(Strings.ADMIN_NOT_EXISTS)
    }

    override fun getCommandName() =
        Commands.CMD_RM_ADMIN
}