package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.Strings
import com.helltar.aibot.database.dao.configurationsDao

class SlowmodeSetting(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        if (arguments.isEmpty() || arguments[0].toIntOrNull() == null) {
            replyToMessage(Strings.SLOWMODE_CURRENT_VALUE.format(configurationsDao.getSlowmodeMaxUsageCount()))
            return
        }

        arguments[0].toIntOrNull()?.let { newMax ->
            if (configurationsDao.updateSlowmodeMaxUsageCount(newMax))
                replyToMessage(Strings.SLOWMODE_SUCCESFULLY_CHANGED.format(newMax))
            else
                replyToMessage(Strings.SLOWMODE_CHANGE_FAIL)
        }
    }

    override fun commandName() =
        Commands.Creator.CMD_SLOWMODE
}
