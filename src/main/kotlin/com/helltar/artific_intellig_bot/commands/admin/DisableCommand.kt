package com.helltar.artific_intellig_bot.commands.admin

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.EXT_DISABLED
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.Commands
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class DisableCommand(bot: Bot, message: Message, args: List<String>) : BotCommand(bot, message, args) {

    override fun run() {
        if (args.isEmpty()) return

        val commandName = args[0]

        if (!Commands.disalableCmdsList.contains(commandName)) {
            sendMessage(String.format(Strings.command_not_available, commandName, Commands.disalableCmdsList))
            return
        }

        File(DIR_DB + commandName + EXT_DISABLED).run {
            if (!exists())
                try {
                    createNewFile()
                    sendMessage(String.format(Strings.command_disabled, commandName))
                } catch (e: IOException) {
                    sendMessage("❌ <code>${e.message}</code>")
                    LoggerFactory.getLogger(javaClass).error(e.message)
                }
            else
                sendMessage(String.format(Strings.command_already_disabled, commandName))
        }
    }
}
