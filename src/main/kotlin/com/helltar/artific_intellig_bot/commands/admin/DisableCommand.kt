package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.EXT_DISABLED
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.Commands
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class DisableCommand(ctx: MessageContext,args: List<String>) : BotCommand(ctx,args) {

    override fun run() {
        if (args.isEmpty()) {
            replyToMessage(Commands.disalableCmdsList.toString())
            return
        }

        val commandName = args[0]

        if (!Commands.disalableCmdsList.contains(commandName)) {
            replyToMessage(String.format(Strings.command_not_available, commandName, Commands.disalableCmdsList))
            return
        }

        File(DIR_DB + commandName + EXT_DISABLED).run {
            if (!exists())
                try {
                    createNewFile()
                    replyToMessage(String.format(Strings.command_disabled, commandName))
                } catch (e: IOException) {
                    replyToMessage("❌ <code>${e.message}</code>")
                    LoggerFactory.getLogger(javaClass).error(e.message)
                }
            else
                replyToMessage(String.format(Strings.command_already_disabled, commandName))
        }
    }
}
