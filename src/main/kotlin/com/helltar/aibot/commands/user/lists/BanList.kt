package com.helltar.aibot.commands.user.lists

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.BanList
import com.helltar.aibot.dao.tables.BanList.reason

class BanList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val list =
            DatabaseFactory.banList.getList().joinToString("\n") {
                val username = it[BanList.username] ?: it[BanList.firstName]
                val reason = it[reason]?.let { reason -> "<i>($reason)</i>" } ?: ""
                "<code>${it[BanList.userId]}</code> <b>$username</b> $reason <i>(${it[BanList.datetime]})</i>"
            }

        replyToMessage(list.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.CMD_BAN_LIST
}