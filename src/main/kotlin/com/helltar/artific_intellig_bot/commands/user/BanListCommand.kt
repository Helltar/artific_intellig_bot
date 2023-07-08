package com.helltar.artific_intellig_bot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database

class BanListCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        replyToMessage(Database.banList.getList().ifEmpty { Strings.list_is_empty })
    }
}
