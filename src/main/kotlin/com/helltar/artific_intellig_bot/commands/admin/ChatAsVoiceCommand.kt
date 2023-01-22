package com.helltar.artific_intellig_bot.commands.admin

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.BotConfig.DIR_DB
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.Commands.commandChatAsText
import com.helltar.artific_intellig_bot.commands.Commands.commandChatAsVoice
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class ChatAsVoiceCommand(bot: Bot, message: Message) : BotCommand(bot, message) {

    override fun run() {
        if (isNotAdmin()) return

        File(DIR_DB + commandChatAsText).run {
            if (exists())
                try {
                    delete()
                    File(DIR_DB + commandChatAsVoice).createNewFile()
                    sendMessage("✅ ChatAsVoice")
                } catch (e: IOException) {
                    sendMessage("❌ <code>${e.message}</code>")
                    LoggerFactory.getLogger(javaClass).error(e.message)
                }
            else
                sendMessage("✅ ChatAsVoice already enabled")
        }
    }
}
