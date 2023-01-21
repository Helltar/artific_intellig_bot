package com.helltar.artific_intellig_bot.commands

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message

class AboutCommand(bot: Bot, message: Message, args: List<String> = listOf()) : BotCommand(bot, message, args) {

    override fun run() {
        sendMessage("""
            <a href="https://github.com/Helltar/artific_intellig_bot">AᎥ</a>
            Contact: @Helltar https://helltar.com
            Source Code:
        """.trimIndent(), disableWebPagePreview = false)
    }
}
