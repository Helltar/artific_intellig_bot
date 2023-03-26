package com.helltar.artific_intellig_bot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext

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
