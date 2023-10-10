package com.helltar.aibot.commands.admin.command

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands.CMD_CHAT_AS_TEXT
import com.helltar.aibot.commands.Commands.CMD_CHAT_AS_VOICE
import com.helltar.aibot.dao.DatabaseFactory

class ChatAsText(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        if (!DatabaseFactory.commandsState.isDisabled(CMD_CHAT_AS_TEXT))
            replyToMessage(Strings.CHAT_AS_TEXT_ALREADY_ENABLED)
        else {
            DatabaseFactory.commandsState.changeState(CMD_CHAT_AS_VOICE, true)
            DatabaseFactory.commandsState.changeState(CMD_CHAT_AS_TEXT, false)
            replyToMessage(Strings.CHAT_AS_TEXT_OK)
        }
    }
}