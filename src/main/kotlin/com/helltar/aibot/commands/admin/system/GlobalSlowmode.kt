package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.configurationsDao

class GlobalSlowmode(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        if (arguments.isEmpty() || arguments[0].toIntOrNull() == null) {
            replyToMessage(Strings.GLOBAL_SLOW_MODE_CURRENT_VALUE.format(configurationsDao.getGlobalSlowmodeMaxUsageCount()))
            return
        }

        val newMax = arguments[0].toInt()

        configurationsDao.setGlobalSlowmodeMaxUsageCount(newMax) // todo: if

        replyToMessage(Strings.GLOBAL_SLOW_MODE_SUCCESFULLY_CHANGED.format(newMax))
    }

    override fun getCommandName() =
        Commands.Creator.CMD_GLOBAL_SLOW_MODE
}
