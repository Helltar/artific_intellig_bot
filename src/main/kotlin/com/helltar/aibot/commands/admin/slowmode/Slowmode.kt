package com.helltar.aibot.commands.admin.slowmode

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.slowmodeDao

class Slowmode(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        if (arguments.isNotEmpty()) {
            if (isReply) {
                val user = replyMessage?.from
                val limit = arguments.first().toIntOrNull()

                if (user != null && limit != null) {
                    if (slowmodeDao.add(user, limit))
                        replyToMessage(String.format(Strings.SLOW_MODE_ON, limit))
                    else {
                        slowmodeDao.update(user, limit)
                        replyToMessage(String.format(Strings.SLOW_MODE_ON_UPDATE, limit))
                    }

                    return
                }
            } else {
                if (arguments.size >= 2) {
                    val userId = arguments[0].toLongOrNull()
                    val limit = arguments[1].toIntOrNull()

                    if (userId != null && limit != null) {
                        if (slowmodeDao.update(userId, limit))
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
        Commands.Admin.CMD_SLOW_MODE
}
