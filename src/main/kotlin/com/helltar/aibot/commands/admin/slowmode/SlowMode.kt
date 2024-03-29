package com.helltar.aibot.commands.admin.slowmode

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class SlowMode(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        if (isReply) {
            val user = message.replyToMessage?.from ?: return
            val limit = args.first().toIntOrNull() ?: return

            if (DatabaseFactory.slowMode.add(user, limit))
                replyToMessage(String.format(Strings.SLOW_MODE_ON, limit))
            else {
                replyToMessage(String.format(Strings.SLOW_MODE_ON_UPDATE, limit))
                DatabaseFactory.slowMode.update(user, limit)
            }
        } else {
            val userId =
                if (args.size >= 2)
                    args.first().toLongOrNull() ?: return
                else
                    return

            val limit = args[1].toIntOrNull() ?: return

            if (DatabaseFactory.slowMode.add(userId, limit))
                replyToMessage(String.format(Strings.SLOW_MODE_ON, limit))
            else
                if (DatabaseFactory.slowMode.update(userId, limit))
                    replyToMessage(String.format(Strings.SLOW_MODE_ON_UPDATE, limit))
        }
    }

    override fun getCommandName() =
        Commands.CMD_SLOW_MODE
}