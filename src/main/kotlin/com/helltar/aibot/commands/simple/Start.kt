package com.helltar.aibot.commands.simple

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand

class Start(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        replyToMessage(
            """
            Welcome to the AI Bot! 🤖✨
 
            To start a conversation, please reply to this message.
        
            How can I assist you today?
            """
                .trimIndent()
        )
    }

    override fun getCommandName() =
        Commands.Simple.CMD_START
}
