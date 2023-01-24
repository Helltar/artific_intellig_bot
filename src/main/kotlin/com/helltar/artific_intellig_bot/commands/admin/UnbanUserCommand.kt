package com.helltar.artific_intellig_bot.commands.admin

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database

class UnbanUserCommand(bot: Bot, message: Message) : BotCommand(bot, message) {

    override fun run() {
        if (isNotAdmin()) return

        val user = message.replyToMessage?.from ?: return

        if (Database.banListTable.unbanUser(user.id) > 0)
            sendMessage("✅ User unbanned")
        else
            sendMessage("✅ User not banned")
    }
}
