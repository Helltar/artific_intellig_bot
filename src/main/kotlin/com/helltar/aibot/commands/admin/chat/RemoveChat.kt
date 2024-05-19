package com.helltar.aibot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class RemoveChat(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val chatId =
            if (args.isNotEmpty())
                args[0].toLongOrNull() ?: return
            else
                ctx.chatId()

        if (DatabaseFactory.chatWhitelistDAO.remove(chatId))
            replyToMessage(Strings.CHAT_REMOVED)
        else
            replyToMessage(Strings.CHAT_NOT_EXISTS)
    }

    override fun getCommandName() =
        Commands.CMD_RM_CHAT
}