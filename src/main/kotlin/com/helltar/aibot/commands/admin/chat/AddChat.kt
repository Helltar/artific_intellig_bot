package com.helltar.aibot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.chatWhitelistDao

class AddChat(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val chatId =
            if (arguments.isNotEmpty())
                arguments[0].toLongOrNull() ?: return
            else
                ctx.chatId()

        val title = if (arguments.size >= 2) arguments[1] else ctx.message().chat.title

        if (chatWhitelistDao.add(chatId, title))
            replyToMessage(Strings.CHAT_ADDED)
        else
            replyToMessage(Strings.CHAT_EXISTS)
    }

    override fun getCommandName() =
        Commands.CMD_ADD_CHAT
}