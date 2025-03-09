package com.helltar.aibot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.chatAllowlistDao

class ChatAllowlist(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val text =
            chatAllowlistDao.list().joinToString("\n") {
                val title = it.title?.let { title -> "<i>($title)</i>" } ?: "null"
                "<code>${it.chatId}</code> $title <i>(${it.createdAt})</i>"
            }

        replyToMessage(text.ifEmpty { Strings.LIST_IS_EMPTY })
    }

    override fun getCommandName() =
        Commands.Admin.CMD_CHAT_ALLOW_LIST
}
