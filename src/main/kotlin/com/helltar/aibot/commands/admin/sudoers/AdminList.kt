package com.helltar.aibot.commands.admin.sudoers

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.sudoersDao
import com.helltar.aibot.db.models.SudoersData

class AdminList(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val list = getFormattedSudoersList(sudoersDao.getList())
        replyToMessage(list.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.CMD_ADMIN_LIST

    private fun getFormattedSudoersList(sudoers: List<SudoersData>) =
        sudoers.joinToString("\n") { sudoer -> formatSudoer(sudoer) }

    private fun formatSudoer(sudoer: SudoersData) =
        "<code>${sudoer.userId}</code> <b>${sudoer.username}</b> <i>(${sudoer.datetime})</i>"
}