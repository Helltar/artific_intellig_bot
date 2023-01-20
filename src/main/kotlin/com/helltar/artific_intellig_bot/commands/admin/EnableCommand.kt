package com.helltar.artific_intellig_bot.commands.admin

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.BotConfig
import com.helltar.artific_intellig_bot.BotConfig.DIR_DB
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.Commands
import java.io.File

class EnableCommand(bot: Bot, message: Message, args: List<String>) : BotCommand(bot, message, args) {

    override fun run() {
        if (isNotAdmin()) return
        if (args.isEmpty()) return

        val commandName = args[0]

        if (!Commands.commandsList.contains(commandName)) {
            sendMessage("Command <b>$commandName</b> not available: ${Commands.commandsList}")
            return
        }

        val file = File(DIR_DB + commandName + BotConfig.EXT_DISABLED)

        if (file.exists())
            if (file.delete())
                sendMessage("✅ Command <b>$commandName</b> enable")
            else
                sendMessage("❌ Error when delete lock file: <code>${file.name}</code>")
        else
            sendMessage("✅ Command <b>$commandName</b> already enabled")
    }
}
