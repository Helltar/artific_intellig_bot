package com.helltar.artific_intellig_bot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Utils

class UptimeCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        replyToMessage(Utils.getSysStat())
    }
}
