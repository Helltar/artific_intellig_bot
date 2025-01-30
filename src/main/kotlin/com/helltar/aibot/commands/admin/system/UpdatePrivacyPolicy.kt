package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Strings.PRIVACY_POLICY_SUCCESFULLY_UPDATED
import com.helltar.aibot.config.Strings.UPDATE_PRIVACY_POLICY_EXAMPLE
import com.helltar.aibot.database.dao.privacyPoliciesDao

class UpdatePrivacyPolicy(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        if (argumentsString.isBlank()) {
            replyToMessage(UPDATE_PRIVACY_POLICY_EXAMPLE)
            return
        }

        privacyPoliciesDao.update(argumentsString)

        replyToMessage(PRIVACY_POLICY_SUCCESFULLY_UPDATED)
    }

    override fun getCommandName() =
        Commands.Creator.CMD_UPDATE_PRIVACY_POLICY
}
