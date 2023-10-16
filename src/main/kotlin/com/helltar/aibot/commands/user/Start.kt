package com.helltar.aibot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands

class Start(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        replyToMessage("https://t.me/+siikRmY3uyE5YTBi", enableWebPagePreview = true)
    }

    override fun getCommandName() =
        Commands.CMD_START
}