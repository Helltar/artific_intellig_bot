package com.helltar.aibot.commands.admin.slowmode

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.slowmodeDao

class SlowmodeOff(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val userId =
            if (arguments.isNotEmpty())
                arguments[0].toLongOrNull()
            else
                message.replyToMessage?.from?.id

        if (slowmodeDao.offSlowMode(userId ?: return))
            replyToMessage(Strings.SLOW_MODE_OFF)
        else
            replyToMessage(Strings.SLOW_MODE_OFF_NOT_ENABLED)
    }

    override fun getCommandName() =
        Commands.CMD_SLOW_MODE_OFF
}