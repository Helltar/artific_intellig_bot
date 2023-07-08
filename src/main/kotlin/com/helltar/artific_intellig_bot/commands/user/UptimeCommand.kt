package com.helltar.artific_intellig_bot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Utils
import com.helltar.artific_intellig_bot.commands.BotCommand

class UptimeCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        replyToMessage(Utils.getSysStat())
    }
}
