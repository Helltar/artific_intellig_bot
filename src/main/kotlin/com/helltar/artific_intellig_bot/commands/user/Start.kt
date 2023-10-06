package com.helltar.artific_intellig_bot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.commands.BotCommand

class Start(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        replyToMessage("https://t.me/+siikRmY3uyE5YTBi", enableWebPagePreview = true)
    }
}