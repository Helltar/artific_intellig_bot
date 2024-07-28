package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory.configurationsDAO

class GlobalSlowmode(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        if (args.isEmpty() || args[0].toIntOrNull() == null) {
            replyToMessage(Strings.GLOBAL_SLOW_MODE_CURRENT_VALUE.format(configurationsDAO.getGlobalSlowmodeMaxUsageCount()))
            return
        }

        val newMax = args[0].toInt()

        configurationsDAO.setGlobalSlowmodeMaxUsageCount(newMax)

        replyToMessage(Strings.GLOBAL_SLOW_MODE_SUCCESFULLY_CHANGED.format(newMax))
    }

    override fun getCommandName() =
        Commands.CMD_GLOBAL_SLOW_MODE
}