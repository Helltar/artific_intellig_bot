package com.helltar.aibot.commands.user.lists

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.SlowmodeTable

class SlowmodeList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val list =
            DatabaseFactory.slowmodeDAO.getList().joinToString("\n") {
                val username = it[SlowmodeTable.username] ?: it[SlowmodeTable.firstName]
                val limit = it[SlowmodeTable.limit]
                val requests = it[SlowmodeTable.requests]
                val lastRequest = it[SlowmodeTable.lastRequestTimestamp]
                "<code>${it[SlowmodeTable.userId]}</code> <b>$username</b> <code>$limit</code> <i>($requests - $lastRequest)</i>"
            }

        replyToMessage(list.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.CMD_SLOW_MODE_LIST
}