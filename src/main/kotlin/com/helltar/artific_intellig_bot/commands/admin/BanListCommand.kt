package com.helltar.artific_intellig_bot.commands.admin

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database

class BanListCommand(bot: Bot, message: Message) : BotCommand(bot, message) {

    override fun run() {
        val list = Database.banListTable.getList().joinToString("\n")

        if (list.isNotEmpty())
            sendMessage(list)
        else
            sendMessage("◻️ List is empty")
    }
}
