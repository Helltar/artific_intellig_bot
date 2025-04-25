package com.helltar.aibot.commands.admin.chat

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.chatAllowlistDao

class RemoveChat(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        val chatId = if (arguments.isNotEmpty()) arguments[0].toLongOrNull() else ctx.chatId()

        chatId?.let {
            if (chatAllowlistDao.remove(it))
                replyToMessage(Strings.CHAT_REMOVED)
            else
                replyToMessage(Strings.CHAT_NOT_EXISTS)
        }
    }

    override fun commandName() =
        Commands.Admin.CMD_RM_CHAT
}
