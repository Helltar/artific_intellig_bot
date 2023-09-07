package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory
import com.helltar.artific_intellig_bot.dao.SudoersTable

class AdminListCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        var list = ""

        DatabaseFactory.sudoers.getList().forEach {
            list += "<code>${it[SudoersTable.userId]}</code> <b>${it[SudoersTable.username]}</b> <i>(${it[SudoersTable.datetime]})</i>\n"
        }

        replyToMessage(list.ifEmpty { Strings.list_is_empty })
    }
}