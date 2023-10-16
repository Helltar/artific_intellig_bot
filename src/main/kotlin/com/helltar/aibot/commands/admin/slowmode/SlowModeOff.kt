package com.helltar.aibot.commands.admin.slowmode

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class SlowModeOff(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull()
            else
                message.replyToMessage?.from?.id

        if (DatabaseFactory.slowMode.off(userId ?: return))
            replyToMessage(Strings.SLOW_MODE_OFF)
        else
            replyToMessage(Strings.SLOW_MODE_OFF_NOT_ENABLED)
    }

    override fun getCommandName() =
        Commands.CMD_SLOW_MODE_OFF
}