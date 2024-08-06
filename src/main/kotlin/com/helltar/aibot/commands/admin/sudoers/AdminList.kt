package com.helltar.aibot.commands.admin.sudoers

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.sudoersDao

class AdminList(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val list =
            sudoersDao.getList().joinToString("\n") {
                "<code>${it.userId}</code> <b>${it.username}</b> <i>(${it.datetime})</i>"
            }

        replyToMessage(list.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.CMD_ADMIN_LIST
}