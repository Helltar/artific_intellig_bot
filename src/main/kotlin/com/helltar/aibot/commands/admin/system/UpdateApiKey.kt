package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Config.PROVIDER_OPENAI_COM
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.apiKeysDao

class UpdateApiKey(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        if (arguments.isEmpty()) {
            replyToMessage(Strings.UPDATE_API_KEYS_COMMAND_EXAMPLE)
            return
        }

        val provider = PROVIDER_OPENAI_COM
        val apiKey = argumentsString

        if (apiKeysDao.getKey(provider) != null) {
            if (apiKeysDao.update(provider, apiKey)) {
                replyToMessage(Strings.PROVIDER_API_KEY_SUCCESS_UPDATE.format(provider))
            } else
                replyToMessage(Strings.API_KEY_FAIL_UPDATE.format("$provider $apiKey"))
        } else {
            if (apiKeysDao.add(provider, apiKey))
                replyToMessage(Strings.PROVIDER_API_KEY_SUCCESS_ADD.format(provider))
            else
                replyToMessage(Strings.API_KEY_FAIL_ADD.format("$provider $apiKey"))
        }
    }

    override fun getCommandName() =
        Commands.CMD_UPDATE_API_KEY
}
