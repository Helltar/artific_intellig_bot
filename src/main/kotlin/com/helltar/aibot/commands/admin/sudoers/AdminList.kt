package com.helltar.aibot.commands.admin.sudoers

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.sudoersDao
import com.helltar.aibot.database.models.SudoersData

class AdminList(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val list = getFormattedSudoersList(sudoersDao.list())
        replyToMessage(list.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.Admin.CMD_ADMIN_LIST

    private fun getFormattedSudoersList(sudoers: List<SudoersData>) =
        sudoers.joinToString("\n") { sudoer -> formatSudoer(sudoer) }

    private fun formatSudoer(sudoer: SudoersData) =
        "<code>${sudoer.userId}</code> <b>${sudoer.username}</b> <i>(${sudoer.createdAt})</i>"
}
