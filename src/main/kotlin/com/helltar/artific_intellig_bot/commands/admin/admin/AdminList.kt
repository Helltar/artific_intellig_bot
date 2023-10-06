package com.helltar.artific_intellig_bot.commands.admin.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory
import com.helltar.artific_intellig_bot.dao.tables.Sudoers

class AdminList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val list =
            DatabaseFactory.sudoers.getList().joinToString("\n") {
                "<code>${it[Sudoers.userId]}</code> <b>${it[Sudoers.username]}</b> <i>(${it[Sudoers.datetime]})</i>"
            }

        replyToMessage(list.ifEmpty { Strings.list_is_empty })
    }
}