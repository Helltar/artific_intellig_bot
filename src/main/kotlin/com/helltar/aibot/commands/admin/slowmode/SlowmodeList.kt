package com.helltar.aibot.commands.admin.slowmode

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.slowmodeDao

class SlowmodeList(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val list =
            slowmodeDao.list().joinToString("\n") {
                val username = it.username ?: it.firstName
                val limit = it.limit
                val usageCount = it.usageCount
                val lastUsage = it.lastUsage
                "<code>${it.userId}</code> <b>$username</b> <code>$limit</code> <i>($usageCount - $lastUsage)</i>"
            }

        replyToMessage(list.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.Admin.CMD_SLOW_MODE_LIST
}
