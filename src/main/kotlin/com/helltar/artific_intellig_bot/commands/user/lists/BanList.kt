package com.helltar.artific_intellig_bot.commands.user.lists

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory
import com.helltar.artific_intellig_bot.dao.tables.BanList
import com.helltar.artific_intellig_bot.dao.tables.BanList.reason

class BanList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val list =
            DatabaseFactory.banList.getList().joinToString("\n") {
                val username = it[BanList.username] ?: it[BanList.firstName]
                val reason = it[reason]?.let { reason -> "<i>($reason)</i>" } ?: ""
                "<code>${it[BanList.userId]}</code> <b>$username</b> $reason <i>(${it[BanList.datetime]})</i>"
            }

        replyToMessage(list.ifEmpty { Strings.list_is_empty })
    }
}