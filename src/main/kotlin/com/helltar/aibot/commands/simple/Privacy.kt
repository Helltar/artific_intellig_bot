package com.helltar.aibot.commands.simple

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.privacyPoliciesDao

class Privacy(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        replyToMessage(privacyPoliciesDao.getPolicyText())
    }

    override fun getCommandName() =
        Commands.CMD_PRIVACY
}
