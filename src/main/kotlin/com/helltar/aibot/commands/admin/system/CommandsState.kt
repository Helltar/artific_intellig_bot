package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class CommandsState(ctx: MessageContext, private val disable: Boolean = false) : BotCommand(ctx) {

    override suspend fun run() {
        if (args.isEmpty()) {
            val text =
                buildString {
                    Commands.disalableCommandsList.forEach { commandName ->
                        append("<code>$commandName</code> ")

                        if (DatabaseFactory.commandsDAO.isDisabled(commandName))
                            append("➖")
                        else
                            append("➕")

                        append("\n")
                    }
                }
            replyToMessage(text)
            return
        }

        val commandName = args[0]

        if (!Commands.disalableCommandsList.contains(commandName)) {
            replyToMessage(String.format(Strings.COMMAND_NOT_AVAILABLE, commandName, Commands.disalableCommandsList))
            return
        }

        if (!disable)
            enable(commandName)
        else
            disable(commandName)
    }

    override fun getCommandName() =
        if (disable) Commands.CMD_DISABLE else Commands.CMD_ENABLE

    private suspend fun enable(commandName: String) {
        if (!DatabaseFactory.commandsDAO.isDisabled(commandName))
            replyToMessage(String.format(Strings.COMMAND_ALREADY_ENABLED, commandName))
        else {
            DatabaseFactory.commandsDAO.changeState(commandName, false)
            replyToMessage(String.format(Strings.COMMAND_ENABLED, commandName))
        }
    }

    private suspend fun disable(commandName: String) {
        if (DatabaseFactory.commandsDAO.isDisabled(commandName))
            replyToMessage(String.format(Strings.COMMAND_ALREADY_DISABLED, commandName))
        else {
            DatabaseFactory.commandsDAO.changeState(commandName, true)
            replyToMessage(String.format(Strings.COMMAND_DISABLED, commandName))
        }
    }
}