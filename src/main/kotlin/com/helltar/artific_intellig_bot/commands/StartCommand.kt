package com.helltar.artific_intellig_bot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext

class StartCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        replyToMessage("https://t.me/+siikRmY3uyE5YTBi", enableWebPagePreview = true)
    }
}