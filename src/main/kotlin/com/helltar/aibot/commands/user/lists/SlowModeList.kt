package com.helltar.aibot.commands.user.lists

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.SlowModeTable

class SlowModeList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val list =
            DatabaseFactory.slowMode.getList().joinToString("\n") {
                val username = it[SlowModeTable.username] ?: it[SlowModeTable.firstName]
                val limit = it[SlowModeTable.limit]
                val requests = it[SlowModeTable.requests]
                val lastRequest = it[SlowModeTable.lastRequestTimestamp]
                "<code>${it[SlowModeTable.userId]}</code> <b>$username</b> <code>$limit</code> <i>($requests - $lastRequest)</i>"
            }

        replyToMessage(list.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.CMD_SLOW_MODE_LIST
}