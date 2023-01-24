package com.helltar.artific_intellig_bot.commands.admin

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database

class BanUserCommand(bot: Bot, message: Message, args: List<String>) : BotCommand(bot, message, args) {

    override fun run() {
        val user = message.replyToMessage?.from ?: return

        val reason = args.joinToString(" ")

        if (Database.banListTable.banUser(user, reason).insertedCount > 0)
            sendMessage("❌ User banned")
        else
            sendMessage("✅ User already banned")
    }
}
