package com.helltar.artific_intellig_bot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory
import com.helltar.artific_intellig_bot.dao.tables.ChatWhiteList

class ChatsWhiteList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val text =
            DatabaseFactory.chatWhiteList.getList().joinToString("\n") {
                val title = it[ChatWhiteList.title]?.let { title -> "<i>($title)</i>" } ?: "null"
                "<code>${it[ChatWhiteList.chatId]}</code> $title <i>(${it[ChatWhiteList.datetime]})</i>"
            }

        replyToMessage(text.ifEmpty { Strings.list_is_empty })
    }
}