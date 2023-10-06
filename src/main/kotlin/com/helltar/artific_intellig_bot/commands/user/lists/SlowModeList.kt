package com.helltar.artific_intellig_bot.commands.user.lists

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory
import com.helltar.artific_intellig_bot.dao.tables.SlowMode

class SlowModeList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val list =
            DatabaseFactory.slowMode.getList().joinToString("\n") {
                val username = it[SlowMode.username] ?: it[SlowMode.firstName]
                val limit = it[SlowMode.limit]
                val requests = it[SlowMode.requests]
                val lastRequest = it[SlowMode.lastRequestTimestamp]
                "<code>${it[SlowMode.userId]}</code> <b>$username</b> <code>$limit</code> <i>($requests - $lastRequest)</i>"
            }

        replyToMessage(list.ifEmpty { Strings.list_is_empty })
    }
}