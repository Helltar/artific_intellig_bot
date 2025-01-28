package com.helltar.aibot.commands.admin.system

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Config.PROVIDER_DEEPSEEK
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.apiKeysDao
import com.helltar.aibot.db.dao.configurationsDao

class DeepSeekState(ctx: MessageContext, private val enable: Boolean = false) : BotCommand(ctx) {

    override suspend fun run() {
        apiKeysDao.getKey(PROVIDER_DEEPSEEK)?.let {
            configurationsDao.setDeepSeekState(enable)
            replyToMessage(if (enable) Strings.DEEPSEEK_ENABLED else Strings.DEEPSEEK_DISABLED)
        }
            ?: replyToMessage(Strings.UPDATE_API_KEYS_COMMAND_EXAMPLE)
    }

    override fun getCommandName() =
        if (enable) Commands.CMD_DEEP_SEEK_ON else Commands.CMD_DEEP_SEEK_OFF
}
