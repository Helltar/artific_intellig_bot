package com.helltar.aibot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.chatWhitelistDao

class ChatsWhitelist(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val text =
            chatWhitelistDao.getList().joinToString("\n") {
                val title = it.title?.let { title -> "<i>($title)</i>" } ?: "null"
                "<code>${it.chatId}</code> $title <i>(${it.datetime})</i>"
            }

        replyToMessage(text.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.CMD_CHATS_WHITE_LIST
}