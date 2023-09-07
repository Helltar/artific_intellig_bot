package com.helltar.artific_intellig_bot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.commands.BotCommand

class AboutCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        replyToMessage(
            """
            <a href="https://github.com/Helltar/artific_intellig_bot">AᎥ</a>
            Contact: @Helltar https://helltar.com
            Source Code:
        """.trimIndent(), enableWebPagePreview = true
        )
    }
}