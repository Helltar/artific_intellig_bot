package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.BotConfig.availableApiProviders
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory

class UpdateApiKey(ctx: MessageContext) : BotCommand(ctx) {

    private val providersHtmlList = availableApiProviders.joinToString("\n") { "<code>$it</code>" }

    override fun run() {
        if (args.size < 2) {
            replyToMessage(Strings.UPDATE_API_KEYS_COMMAND_EXAMPLE.format(getCommandName(), availableApiProviders.first()))
            reply(providersHtmlList)
            return
        }

        val provider = args[0]
        val apiKey = args[1]

        if (DatabaseFactory.apiKeys.update(provider, apiKey)) {
            replyToMessage(Strings.PROVIDER_API_KEY_SUCCESS_ADD.format(provider))
        } else {
            replyToMessage(Strings.API_KEY_FAILD_ADD.format(provider))
            reply(providersHtmlList)
        }
    }

    override fun getCommandName() =
        Commands.CMD_UPDATE_API_KEY
}