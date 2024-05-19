package com.helltar.aibot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.ChatWhitelistTable

class ChatsWhitelist(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val text =
            DatabaseFactory.chatWhitelistDAO.getList().joinToString("\n") {
                val title = it[ChatWhitelistTable.title]?.let { title -> "<i>($title)</i>" } ?: "null"
                "<code>${it[ChatWhitelistTable.chatId]}</code> $title <i>(${it[ChatWhitelistTable.datetime]})</i>"
            }

        replyToMessage(text.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.CMD_CHATS_WHITE_LIST
}