package com.helltar.artific_intellig_bot.commands

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.*

abstract class BotCommand(val bot: Bot, val message: Message, val args: List<String>) {

    protected val userId = message.from!!.id
    private val chatId = ChatId.fromId(message.chat.id)
    private val replyToMessageId = message.messageId

    abstract fun run()

    protected fun sendMessage(
        text: String, replyTo: Long = replyToMessageId,
        disableWebPagePreview: Boolean = true, replyMarkup: ReplyMarkup? = null
    ) =
        bot.sendMessage(
            chatId, text, ParseMode.HTML, disableWebPagePreview,
            replyToMessageId = replyTo, allowSendingWithoutReply = true,
            replyMarkup = replyMarkup
        ).get().messageId

    protected fun sendPhoto(photo: TelegramFile, caption: String, replyTo: Long = replyToMessageId) =
        bot.sendPhoto(
            chatId, photo, caption, replyToMessageId = replyTo, allowSendingWithoutReply = true
        )
}
