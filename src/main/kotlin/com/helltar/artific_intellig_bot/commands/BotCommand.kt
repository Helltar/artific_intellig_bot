package com.helltar.artific_intellig_bot.commands

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.*
import com.helltar.artific_intellig_bot.BotConfig.DIR_DB
import com.helltar.artific_intellig_bot.BotConfig.EXT_DISABLED
import com.helltar.artific_intellig_bot.BotConfig.SUDOERS
import java.io.File

abstract class BotCommand(val bot: Bot, val message: Message, val args: List<String>) {

    protected val userId = message.from!!.id
    private val chatId = ChatId.fromId(message.chat.id)
    private val replyToMessageId = message.messageId

    abstract fun run()

    fun isCommandEnable(commandName: String): Boolean {
        if (!isNotAdmin()) return true
        return !File(DIR_DB + commandName + EXT_DISABLED).exists()
    }

    fun sendMessage(
        text: String, replyTo: Long = replyToMessageId,
        disableWebPagePreview: Boolean = true, replyMarkup: ReplyMarkup? = null
    ) =
        bot.sendMessage(
            chatId, text, ParseMode.HTML, disableWebPagePreview,
            replyToMessageId = replyTo, allowSendingWithoutReply = true,
            replyMarkup = replyMarkup
        ).get().messageId

    protected fun isNotAdmin() =
        !SUDOERS.contains(userId.toString())

    protected fun sendPhoto(photo: TelegramFile, caption: String, replyTo: Long = replyToMessageId) =
        bot.sendPhoto(
            chatId, photo, caption, replyToMessageId = replyTo, allowSendingWithoutReply = true
        )
}
