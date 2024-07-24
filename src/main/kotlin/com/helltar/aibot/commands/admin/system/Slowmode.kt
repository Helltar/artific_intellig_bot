package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class Slowmode(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        if (args.isNotEmpty()) {
            if (isReply) {
                val user = replyMessage?.from
                val limit = args.first().toIntOrNull()

                if (user != null && limit != null) {
                    if (DatabaseFactory.slowmodeDAO.add(user, limit))
                        replyToMessage(String.format(Strings.SLOW_MODE_ON, limit))
                    else {
                        DatabaseFactory.slowmodeDAO.update(user, limit)
                        replyToMessage(String.format(Strings.SLOW_MODE_ON_UPDATE, limit))
                    }

                    return
                }
            } else {
                if (args.size >= 2) {
                    val userId = args[0].toLongOrNull()
                    val limit = args[1].toIntOrNull()

                    if (userId != null && limit != null) {
                        if (DatabaseFactory.slowmodeDAO.update(userId, limit))
                            replyToMessage(Strings.SLOW_MODE_ON_UPDATE.format(limit))
                        else
                            replyToMessage(Strings.SLOW_MODE_USER_NOT_FOUND)

                        return
                    }
                }
            }
        }

        replyToMessage(Strings.SLOW_MODE_BAD_ARG)
    }

    override fun getCommandName() =
        Commands.CMD_SLOW_MODE
}