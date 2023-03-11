package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.EXT_DISABLED
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.Commands
import java.io.File

class EnableCommand(ctx: MessageContext, args: List<String>) : BotCommand(ctx, args) {

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
            if (exists())
                if (delete())
                    replyToMessage(String.format(Strings.command_enabled, commandName))
                else
                    replyToMessage(String.format(Strings.error_delete_lock_file, name))
            else
                replyToMessage(String.format(Strings.command_already_enabled, commandName))
        }
    }
}
