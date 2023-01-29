package com.helltar.artific_intellig_bot.commands.admin

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database
import java.util.concurrent.TimeUnit

class UnbanUserCommand(bot: Bot, message: Message, args: List<String>) : BotCommand(bot, message, args) {

    override fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull()
            else
                message.replyToMessage?.from?.id

        val messageId =
            if (Database.banListTable.unbanUser(userId ?: return) > 0)
                sendMessage("✅ User unbanned")
            else
                sendMessage("✅ User not banned")

        if (message.chat.type != "private") {
            TimeUnit.SECONDS.sleep(3)
            deleteMessage(messageId)
        }
    }
}
