package com.helltar.aibot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.BotCommand

class About(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        replyToMessage(
            """
            <a href="https://github.com/Helltar/artific_intellig_bot">AᎥ</a>
            Contact: @Helltar https://helltar.com
            Source Code:
            """
                .trimIndent(), enableWebPagePreview = true
        )
    }
}