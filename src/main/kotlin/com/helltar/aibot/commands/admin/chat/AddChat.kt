package com.helltar.aibot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class AddChat(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val chatId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                ctx.chatId()

        val title = if (args.size >= 2) args[1] else ctx.message().chat.title

        if (DatabaseFactory.chatWhitelistDAO.add(chatId, title))
            replyToMessage(Strings.CHAT_ADDED)
        else
            replyToMessage(Strings.CHAT_EXISTS)
    }

    override fun getCommandName() =
        Commands.CMD_ADD_CHAT
}