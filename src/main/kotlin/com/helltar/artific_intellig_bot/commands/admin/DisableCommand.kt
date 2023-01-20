package com.helltar.artific_intellig_bot.commands.admin

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.BotConfig.DIR_DB
import com.helltar.artific_intellig_bot.BotConfig.EXT_DISABLED
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.Commands
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class DisableCommand(bot: Bot, message: Message, args: List<String>) : BotCommand(bot, message, args) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        if (isNotAdmin()) return
        if (args.isEmpty()) return

        val commandName = args[0]

        if (!Commands.commandsList.contains(commandName)) {
            sendMessage("Command <b>$commandName</b> not available: ${Commands.commandsList}")
            return
        }

        val file = File(DIR_DB + commandName + EXT_DISABLED)

        if (!file.exists())
            try {
                file.createNewFile()
                sendMessage("✅ Command <b>$commandName</b> disabled")
            } catch (e: IOException) {
                sendMessage("❌ <code>${e.message}</code>")
                log.error(e.message)
            }
        else
            sendMessage("✅ Command <b>$commandName</b> already disabled")
    }
}
