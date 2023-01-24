package com.helltar.artific_intellig_bot.commands.admin

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.EXT_DISABLED
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.Commands
import java.io.File

class EnableCommand(bot: Bot, message: Message, args: List<String>) : BotCommand(bot, message, args) {

    override fun run() {
        if (args.isEmpty()) return

        val commandName = args[0]

        if (!Commands.disalableCmdsList.contains(commandName)) {
            sendMessage("Command <b>$commandName</b> not available: ${Commands.disalableCmdsList}")
            return
        }

        File(DIR_DB + commandName + EXT_DISABLED).run {
            if (exists())
                if (delete())
                    sendMessage("✅ Command <b>$commandName</b> enable")
                else
                    sendMessage("❌ Error when delete lock file: <code>$name</code>")
            else
                sendMessage("✅ Command <b>$commandName</b> already enabled")
        }
    }
}
