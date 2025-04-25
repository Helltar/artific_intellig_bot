package com.helltar.aibot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.chatAllowlistDao

class AddChat(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val chatId = if (arguments.isNotEmpty()) arguments[0].toLongOrNull() else ctx.chatId()

        chatId?.let {
            val title = if (arguments.size >= 2) arguments[1] else ctx.message().chat.title

            if (chatAllowlistDao.add(it, title))
                replyToMessage(Strings.CHAT_ADDED)
            else
                replyToMessage(Strings.CHAT_EXISTS)
        }
    }

    override fun commandName() =
        Commands.Creator.CMD_ADD_CHAT
}
