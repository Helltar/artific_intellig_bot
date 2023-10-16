package com.helltar.aibot.commands.user.lists

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.BanListTable
import com.helltar.aibot.dao.tables.BanListTable.reason

class BanList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val list =
            DatabaseFactory.banList.getList().joinToString("\n") {
                val username = it[BanListTable.username] ?: it[BanListTable.firstName]
                val reason = it[reason]?.let { reason -> "<i>($reason)</i>" } ?: ""
                "<code>${it[BanListTable.userId]}</code> <b>$username</b> $reason <i>(${it[BanListTable.datetime]})</i>"
            }

        replyToMessage(list.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.CMD_BAN_LIST
}