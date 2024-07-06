package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings.PRIVACY_POLICY_SUCCESFULLY_UPDATED
import com.helltar.aibot.Strings.UPDATE_PRIVACY_POLICY_EXAMPLE
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.PrivacyPoliciesDAO

class UpdatePrivacyPolicy(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        if (argsText.isBlank()) {
            replyToMessage(UPDATE_PRIVACY_POLICY_EXAMPLE)
            return
        }

        PrivacyPoliciesDAO().update(argsText)

        replyToMessage(PRIVACY_POLICY_SUCCESFULLY_UPDATED)
    }

    override fun getCommandName() =
        Commands.CMD_UPDATE_PRIVACY_POLICY
}