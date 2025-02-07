package com.helltar.aibot.commands.simple

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand

class About(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        replyToMessage(
            """
            <a href="https://github.com/Helltar/artific_intellig_bot">AᎥ</a>
            Contact: https://helltar.com
            Source Code:
            """
                .trimIndent(), webPagePreview = true
        )
    }

    override fun getCommandName() =
        Commands.Simple.CMD_ABOUT
}
