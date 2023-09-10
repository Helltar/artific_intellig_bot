package com.helltar.artific_intellig_bot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory
import com.helltar.artific_intellig_bot.dao.SlowModeTable

class SlowModeListCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        var list = ""

        DatabaseFactory.slowMode.list().forEach {
            val username = it[SlowModeTable.username] ?: it[SlowModeTable.firstName]
            val limit = it[SlowModeTable.limit]
            val requests = it[SlowModeTable.requests]
            val lastRequest = it[SlowModeTable.lastRequestTimestamp]
            list += "<code>${it[SlowModeTable.userId]}</code> <b>$username</b> <code>$limit</code> <i>$requests - $lastRequest</i>\n"
        }

        replyToMessage(list.ifEmpty { Strings.list_is_empty })
    }
}