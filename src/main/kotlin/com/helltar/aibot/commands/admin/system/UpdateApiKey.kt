package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.DatabaseFactory.apiKeysProviders
import com.helltar.aibot.dao.tables.ApiKeyType

class UpdateApiKey(ctx: MessageContext) : BotCommand(ctx) {

    private val providersHtmlList = apiKeysProviders.joinToString("\n") { "<code>$it</code>" }

    override suspend fun run() {
        if (args.size < 2) {
            replyToMessage(Strings.UPDATE_API_KEYS_COMMAND_EXAMPLE.format(getCommandName(), apiKeysProviders.first()))
            reply(providersHtmlList)
            return
        }

        val provider = args[0]

        if (provider !in apiKeysProviders) {
            reply(providersHtmlList)
            return
        }

        val apiKey = args[1]
        var type = ApiKeyType.USER

        if (args.size >= 3) {
            type =
                when (args[2].toIntOrNull()) {
                    0 -> ApiKeyType.CREATOR
                    1 -> ApiKeyType.ADMIN
                    else -> type
                }
        }

        if (DatabaseFactory.apiKeysDAO.getKey(provider, type) != null) {
            if (DatabaseFactory.apiKeysDAO.update(provider, apiKey, type)) {
                replyToMessage(Strings.PROVIDER_API_KEY_SUCCESS_UPDATE.format(provider, type.toString()))
            } else
                replyToMessage(Strings.API_KEY_FAIL_UPDATE.format("$provider $type $apiKey"))
        } else {
            if (DatabaseFactory.apiKeysDAO.add(provider, apiKey, type))
                replyToMessage(Strings.PROVIDER_API_KEY_SUCCESS_ADD.format(provider, type.toString()))
            else
                replyToMessage(Strings.API_KEY_FAIL_ADD.format("$provider $type $apiKey"))
        }
    }

    override fun getCommandName() =
        Commands.CMD_UPDATE_API_KEY
}