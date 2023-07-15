package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Commands
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.EXT_DISABLED
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class ChangeStateCommand(ctx: MessageContext, private val disable: Boolean = false) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run() {
        var disalableCmdsList = ""

        Commands.disalableCmdsList.forEach {
            disalableCmdsList += "<code>$it</code> "
        }

        if (args.isEmpty()) {
            replyToMessage(disalableCmdsList)
            return
        }

        val commandName = args[0]

        if (!Commands.disalableCmdsList.contains(commandName)) {
            replyToMessage(String.format(Strings.command_not_available, commandName, disalableCmdsList))
            return
        }

        val lockFile = File("$DIR_DB/$commandName$EXT_DISABLED")

        if (!disable)
            enable(lockFile, commandName)
        else
            disable(lockFile, commandName)
    }

    private fun enable(file: File, commandName: String) {
        if (file.exists()) {
            if (file.delete())
                replyToMessage(String.format(Strings.command_enabled, commandName))
            else
                replyToMessage(String.format(Strings.error_delete_lock_file, file.name))
        } else
            replyToMessage(String.format(Strings.command_already_enabled, commandName))
    }

    private fun disable(file: File, commandName: String) {
        if (!file.exists()) {
            try {
                file.createNewFile()
                replyToMessage(String.format(Strings.command_disabled, commandName))
            } catch (e: IOException) {
                replyToMessage("❌ <code>${e.message}</code>")
                log.error(e.message)
            }
        } else
            replyToMessage(String.format(Strings.command_already_disabled, commandName))
    }
}