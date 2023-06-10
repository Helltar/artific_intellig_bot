package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.db.Database
import java.util.concurrent.TimeUnit

class UnbanUserCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        val userId =
            if (args.isNotEmpty())
                args[0].toLongOrNull()
            else
                ctx.message().replyToMessage?.from?.id

        val messageId =
            if (Database.banList.unbanUser(userId ?: return))
                replyToMessage(Strings.user_unbanned)
            else
                replyToMessage(Strings.user_not_banned)

        if (ctx.message().chat.type != "private") {
            TimeUnit.SECONDS.sleep(3)
            deleteMessage(messageId)
        }
    }
}
