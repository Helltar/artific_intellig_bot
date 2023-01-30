package com.helltar.artific_intellig_bot.commands.admin

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsText
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsVoice
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class ChatAsVoiceCommand(bot: Bot, message: Message) : BotCommand(bot, message) {

    override fun run() {
        File(DIR_DB + cmdChatAsText).run {
            if (exists())
                try {
                    delete()
                    File(DIR_DB + cmdChatAsVoice).createNewFile()
                    sendMessage(Strings.chat_as_voice_ok)
                } catch (e: IOException) {
                    sendMessage("❌ <code>${e.message}</code>")
                    LoggerFactory.getLogger(javaClass).error(e.message)
                }
            else
                sendMessage(Strings.chat_as_voice_already_enabled)
        }
    }
}
