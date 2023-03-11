package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database

class AdminListCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        replyToMessage(Database.sudoers.getList().ifEmpty { Strings.list_is_empty })
    }
}
