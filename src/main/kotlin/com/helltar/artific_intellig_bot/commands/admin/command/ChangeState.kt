package com.helltar.artific_intellig_bot.commands.admin.command

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Commands
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class ChangeState(ctx: MessageContext, private val disable: Boolean = false) : BotCommand(ctx) {

    override fun run() {
        val disalableCmdsList = Commands.disalableCmdsList.toString()

        if (args.isEmpty()) {
            replyToMessage(disalableCmdsList)
            return
        }

        val commandName = args[0]

        if (!Commands.disalableCmdsList.contains(commandName)) {
            replyToMessage(String.format(Strings.command_not_available, commandName, disalableCmdsList))
            return
        }

        if (!disable)
            enable(commandName)
        else
            disable(commandName)
    }

    private fun enable(commandName: String) {
        if (!DatabaseFactory.commandsState.isDisabled(commandName))
            replyToMessage(String.format(Strings.command_already_enabled, commandName))
        else {
            DatabaseFactory.commandsState.changeState(commandName, false)
            replyToMessage(String.format(Strings.command_enabled, commandName))
        }
    }

    private fun disable(commandName: String) {
        if (DatabaseFactory.commandsState.isDisabled(commandName))
            replyToMessage(String.format(Strings.command_already_disabled, commandName))
        else {
            DatabaseFactory.commandsState.changeState(commandName, true)
            replyToMessage(String.format(Strings.command_disabled, commandName))
        }
    }
}