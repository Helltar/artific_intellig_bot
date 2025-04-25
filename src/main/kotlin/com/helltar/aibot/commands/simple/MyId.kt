package com.helltar.aibot.commands.simple

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand

class MyId(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        replyToMessage("<code>$userId</code>")
    }

    override fun commandName() =
        Commands.Simple.CMD_MYID
}
