package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Commands.cmdChatAsTextName
import com.helltar.artific_intellig_bot.Commands.cmdChatAsVoiceName
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class ChatAsVoiceCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        if (!DatabaseFactory.commandsState.isDisabled(cmdChatAsVoiceName))
            replyToMessage(Strings.chat_as_voice_already_enabled)
        else {
            DatabaseFactory.commandsState.changeState(cmdChatAsTextName, true)
            DatabaseFactory.commandsState.changeState(cmdChatAsVoiceName, false)
            replyToMessage(Strings.chat_as_voice_ok)
        }
    }
}