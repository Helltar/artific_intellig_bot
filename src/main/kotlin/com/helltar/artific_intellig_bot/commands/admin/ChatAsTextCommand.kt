package com.helltar.artific_intellig_bot.commands.admin

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.Strings
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsText
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsVoice
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class ChatAsTextCommand(ctx: MessageContext) : BotCommand(ctx) {

    override fun run() {
        File(DIR_DB + cmdChatAsVoice).run {
            if (exists()) {
                delete()
                createChatAsTextLockFile()
            } else
                if (File(DIR_DB + cmdChatAsText).exists())
                    replyToMessage(Strings.chat_as_text_already_enabled)
                else
                    createChatAsTextLockFile()
        }
    }

    private fun createChatAsTextLockFile() {
        try {
            File(DIR_DB + cmdChatAsText).createNewFile()
            replyToMessage(Strings.chat_as_text_ok)
        } catch (e: IOException) {
            replyToMessage("❌ <code>${e.message}</code>")
            LoggerFactory.getLogger(javaClass).error(e.message)
        }
    }
}
