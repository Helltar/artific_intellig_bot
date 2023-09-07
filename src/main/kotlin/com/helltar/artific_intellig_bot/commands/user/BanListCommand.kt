package com.helltar.artific_intellig_bot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.BanListTable
import com.helltar.artific_intellig_bot.dao.BanListTable.reason
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class BanListCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        var list = ""

        DatabaseFactory.banList.getList().forEach {
            val username = it[BanListTable.username] ?: it[BanListTable.firstName]
            val reason = it[reason]?.let { "<i>($this)</i>" } ?: ""
            list += "<code>${it[BanListTable.userId]}</code> <b>$username</b> $reason <i>(${it[BanListTable.datetime]})</i>\n"
        }

        replyToMessage(list.ifEmpty { Strings.list_is_empty })
    }
}