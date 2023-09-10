package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class SlowModeCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val user = message.replyToMessage?.from ?: return
        val limit = argsText.toIntOrNull() ?: return

        if (DatabaseFactory.slowMode.add(user, limit))
            replyToMessage(String.format(Strings.slow_mode_on, limit))
        else {
            replyToMessage(String.format(Strings.slow_mode_on_update, limit))
            DatabaseFactory.slowMode.update(user, limit)
        }
    }
}