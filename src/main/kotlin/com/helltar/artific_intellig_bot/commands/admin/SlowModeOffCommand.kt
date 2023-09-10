package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class SlowModeOffCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull()
            else
                message.replyToMessage?.from?.id

        if (DatabaseFactory.slowMode.off(userId ?: return))
            replyToMessage(Strings.slow_mode_off)
        else
            replyToMessage(Strings.slow_mode_off_not_enabled)
    }
}