package com.helltar.aibot.commands.simple

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.database.dao.privacyPoliciesDao

class Privacy(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        replyToMessage(privacyPoliciesDao.getPolicyText())
    }

    override fun getCommandName() =
        Commands.Simple.CMD_PRIVACY
}
