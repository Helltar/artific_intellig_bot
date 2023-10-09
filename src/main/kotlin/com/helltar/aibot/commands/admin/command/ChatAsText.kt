package com.helltar.aibot.commands.admin.command

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands.cmdChatAsText
import com.helltar.aibot.commands.Commands.cmdChatAsVoice
import com.helltar.aibot.dao.DatabaseFactory

class ChatAsText(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        if (!DatabaseFactory.commandsState.isDisabled(cmdChatAsText))
            replyToMessage(Strings.chat_as_text_already_enabled)
        else {
            DatabaseFactory.commandsState.changeState(cmdChatAsVoice, true)
            DatabaseFactory.commandsState.changeState(cmdChatAsText, false)
            replyToMessage(Strings.chat_as_text_ok)
        }
    }
}