package com.helltar.aibot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.ChatWhiteList

class ChatsWhiteList(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val text =
            DatabaseFactory.chatWhiteList.getList().joinToString("\n") {
                val title = it[ChatWhiteList.title]?.let { title -> "<i>($title)</i>" } ?: "null"
                "<code>${it[ChatWhiteList.chatId]}</code> $title <i>(${it[ChatWhiteList.datetime]})</i>"
            }

        replyToMessage(text.ifEmpty { Strings.LIST_IS_EMPTY })
    }
}