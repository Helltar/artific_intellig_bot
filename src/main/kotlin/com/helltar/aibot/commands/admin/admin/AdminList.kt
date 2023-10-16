package com.helltar.aibot.commands.admin.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.Sudoers

class AdminList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val list =
            DatabaseFactory.sudoers.getList().joinToString("\n") {
                "<code>${it[Sudoers.userId]}</code> <b>${it[Sudoers.username]}</b> <i>(${it[Sudoers.datetime]})</i>"
            }

        replyToMessage(list.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.CMD_ADMIN_LIST
}