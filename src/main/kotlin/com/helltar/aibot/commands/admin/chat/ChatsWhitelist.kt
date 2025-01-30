package com.helltar.aibot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.chatWhitelistDao

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
        Commands.Admin.CMD_CHATS_WHITE_LIST
}
