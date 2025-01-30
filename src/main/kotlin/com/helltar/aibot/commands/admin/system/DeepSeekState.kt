package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Config.API_KEY_PROVIDER_DEEPSEEK
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.apiKeyDao
import com.helltar.aibot.database.dao.configurationsDao

class DeepSeekState(ctx: MessageContext, private val enable: Boolean = false) : BotCommand(ctx) {

    override suspend fun run() {
        apiKeyDao.getKey(API_KEY_PROVIDER_DEEPSEEK)?.let {
            configurationsDao.setDeepSeekState(enable)
            replyToMessage(if (enable) Strings.DEEPSEEK_ENABLED else Strings.DEEPSEEK_DISABLED)
        }
            ?: replyToMessage(Strings.UPDATE_API_KEYS_COMMAND_EXAMPLE)
    }

    override fun getCommandName() =
        if (enable)
            Commands.Creator.CMD_DEEP_SEEK_ON
        else
            Commands.Creator.CMD_DEEP_SEEK_OFF
}
