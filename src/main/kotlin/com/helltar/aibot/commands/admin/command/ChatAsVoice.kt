package com.helltar.aibot.commands.admin.command

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands.CMD_CHAT_AS_TEXT
import com.helltar.aibot.commands.Commands.CMD_CHAT_AS_VOICE
import com.helltar.aibot.dao.DatabaseFactory

class ChatAsVoice(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        if (!DatabaseFactory.commandsState.isDisabled(CMD_CHAT_AS_VOICE))
            replyToMessage(Strings.CHAT_AS_VOICE_ALREADY_ENABLED)
        else {
            DatabaseFactory.commandsState.changeState(CMD_CHAT_AS_TEXT, true)
            DatabaseFactory.commandsState.changeState(CMD_CHAT_AS_VOICE, false)
            replyToMessage(Strings.CHAT_AS_VOICE_OK)
        }
    }
}