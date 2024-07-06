package com.helltar.aibot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.PrivacyPoliciesDAO

class Privacy(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        replyToMessage(PrivacyPoliciesDAO().getPolicyText())
    }

    override fun getCommandName() =
        Commands.CMD_PRIVACY
}