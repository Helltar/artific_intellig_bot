package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.Commands.cmdChatAsText
import com.helltar.artific_intellig_bot.Commands.cmdChatAsVoice
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory

class ChatAsTextCommand(ctx: MessageContext) : BotCommand(ctx) {

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