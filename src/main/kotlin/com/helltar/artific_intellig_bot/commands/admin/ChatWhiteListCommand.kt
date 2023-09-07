package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.ChatWhiteListTable
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class ChatWhiteListCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        var text = ""

        DatabaseFactory.chatWhiteList.getList().forEach {
            val title = it[ChatWhiteListTable.title]?.let { title -> "<i>($title)</i>" } ?: "null"
            text += "<code>${it[ChatWhiteListTable.chatId]}</code> $title <i>(${it[ChatWhiteListTable.datetime]})</i>\n"
        }

        replyToMessage(text.ifEmpty { Strings.list_is_empty })
    }
}