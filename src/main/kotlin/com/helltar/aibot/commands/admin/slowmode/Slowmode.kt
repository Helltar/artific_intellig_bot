package com.helltar.aibot.commands.admin.slowmode

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class Slowmode(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        if (args.isEmpty()) {
            helpMessage()
            return
        }

        if (isReply) {
            message.replyToMessage?.from?.let { user ->
                args.first().toIntOrNull()?.let { limit ->
                    if (DatabaseFactory.slowmodeDAO.add(user, limit))
                        replyToMessage(String.format(Strings.SLOW_MODE_ON, limit))
                    else {
                        DatabaseFactory.slowmodeDAO.update(user, limit)
                        replyToMessage(String.format(Strings.SLOW_MODE_ON_UPDATE, limit))
                    }
                }
            }
        } else {
            if (args.size < 3) {
                helpMessage()
                return
            }

            val userId = args[0].toLongOrNull()
            val limit = args[1].toIntOrNull()
            val username = args[2]

            if (userId != null && limit != null && username.isNotBlank()) {
                if (DatabaseFactory.slowmodeDAO.add(userId, username, limit))
                    replyToMessage(Strings.SLOW_MODE_ON.format(limit))
                else
                    if (DatabaseFactory.slowmodeDAO.update(userId, username, limit))
                        replyToMessage(Strings.SLOW_MODE_ON_UPDATE.format(limit))
            } else
                helpMessage()
        }
    }

    private fun helpMessage() =
        replyToMessage(Strings.SLOW_MODE_BAD_ARG)

    override fun getCommandName() =
        Commands.CMD_SLOW_MODE
}