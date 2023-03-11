package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database
import java.util.concurrent.TimeUnit

class BanUserCommand(ctx: MessageContext, args: List<String> = listOf()) : BotCommand(ctx, args) {

    override fun run() {
        val user = ctx.message().replyToMessage?.from ?: return

        val reason = args.joinToString(" ")

        val messageId =
            if (Database.banList.banUser(user, reason))
                replyToMessage(Strings.user_banned)
            else
                replyToMessage(Strings.user_already_banned)

        if (ctx.message().chat.type != "private") {
            TimeUnit.SECONDS.sleep(3)
            deleteMessage(messageId)
        }
    }
}
