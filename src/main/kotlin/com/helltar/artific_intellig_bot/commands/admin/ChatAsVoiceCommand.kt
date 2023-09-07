package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Commands.cmdChatAsText
import com.helltar.artific_intellig_bot.Commands.cmdChatAsVoice
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class ChatAsVoiceCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        if (!DatabaseFactory.commandsState.isDisabled(cmdChatAsVoice))
            replyToMessage(Strings.chat_as_voice_already_enabled)
        else {
            DatabaseFactory.commandsState.changeState(cmdChatAsText, true)
            DatabaseFactory.commandsState.changeState(cmdChatAsVoice, false)
            replyToMessage(Strings.chat_as_voice_ok)
        }
    }
}