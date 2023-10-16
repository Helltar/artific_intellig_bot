package com.helltar.aibot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands

class MyId(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        replyToMessage("<code>$userId</code>")
    }

    override fun getCommandName() =
        Commands.CMD_MYID
}