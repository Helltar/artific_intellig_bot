package com.helltar.aibot.commands.admin.sudoers

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.SudoersTable

class AdminList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val list =
            DatabaseFactory.sudoersDAO.getList().joinToString("\n") {
                "<code>${it[SudoersTable.userId]}</code> <b>${it[SudoersTable.username]}</b> <i>(${it[SudoersTable.datetime]})</i>"
            }

        replyToMessage(list.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.CMD_ADMIN_LIST
}