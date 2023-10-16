package com.helltar.aibot.commands.admin.slowmode

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class SlowMode(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val user = message.replyToMessage?.from ?: return
        val limit = argsText.toIntOrNull() ?: return

        if (DatabaseFactory.slowMode.add(user, limit))
            replyToMessage(String.format(Strings.SLOW_MODE_ON, limit))
        else {
            replyToMessage(String.format(Strings.SLOW_MODE_ON_UPDATE, limit))
            DatabaseFactory.slowMode.update(user, limit)
        }
    }

    override fun getCommandName() =
        Commands.CMD_SLOW_MODE
}