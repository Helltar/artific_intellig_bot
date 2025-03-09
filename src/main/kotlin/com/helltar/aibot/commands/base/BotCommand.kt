package com.helltar.aibot.commands.base

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.config.Config.API_KEY_PROVIDER_OPENAI
import com.helltar.aibot.config.Config.creatorId
import com.helltar.aibot.config.Config.telegramBotUsername
import com.helltar.aibot.database.dao.*
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.message.Message
import java.io.File
import java.io.InputStream
import java.util.concurrent.CompletableFuture

abstract class BotCommand(val ctx: MessageContext) : Command {

    private companion object {
        const val MAX_MESSAGE_LENGTH = 4096
    }

    val userLanguageCode = ctx.user().languageCode ?: "en"

    protected val userId = ctx.user().id
    protected val message = ctx.message()
    protected val replyMessage: Message? = message.replyToMessage
    protected val isReply = message.isReply && message.replyToMessage.messageId != message.replyToMessage.messageThreadId // todo: tmp. fix, check.
    protected val isNotReply = !isReply

    protected val arguments: Array<String> = ctx.arguments()
    protected val argumentsString: String = ctx.argumentsAsString()

    suspend fun isCommandDisabled(command: String) =
        commandsDao.isDisabled(command)

    suspend fun isChatInAllowlistList() =
        chatAllowlistDao.isExists(ctx.chatId())

    suspend fun isUserBanned(userId: Long) =
        banlistDao.isBanned(userId)

    suspend fun isAdmin() =
        sudoersDao.isAdmin(userId)

    fun isCreator(userId: Long = this.userId) =
        userId == creatorId

    fun isNotMyMessage(message: Message?) =
        message?.from?.userName != telegramBotUsername

    fun replyToMessage(text: String, messageId: Int = message.messageId, webPagePreview: Boolean = false, markdown: Boolean = false) {
        val parseMode = if (markdown) ParseMode.MARKDOWN else ParseMode.HTML
        val messageIdToReply = if (replyMessage?.from?.isBot == false) messageId else message.messageId // todo: refact.

        if (text.length <= MAX_MESSAGE_LENGTH) {
            ctx.replyToMessage(text)
                .setReplyToMessageId(messageIdToReply)
                .setParseMode(parseMode)
                .setWebPagePreviewEnabled(webPagePreview)
                .call(ctx.sender)
        } else
            chunkedReplyToMessage(text, messageIdToReply, webPagePreview, parseMode)
    }

    fun replyToMessageWithDocument(fileId: String, caption: String): Int =
        ctx.replyWithDocument()
            .setFile(fileId)
            .setCaption(caption)
            .setReplyToMessageId(message.messageId)
            .call(ctx.sender)
            .messageId

    fun deleteMessage(messageId: Int): CompletableFuture<Boolean> =
        ctx.deleteMessage().setMessageId(messageId).callAsync(ctx.sender)

    fun sendDocument(file: File): Message =
        ctx.replyWithDocument()
            .setFile(file)
            .call(ctx.sender)

    protected suspend fun openaiKey() =
        getApiKey(API_KEY_PROVIDER_OPENAI)

    protected suspend fun getApiKey(provider: String) =
        apiKeyDao.getKey(provider)

    protected suspend fun isDeepSeekNotEnabled() =
        !configurationsDao.isDeepSeekEnabled()

    protected fun replyToMessageWithPhoto(url: String, caption: String = "", messageId: Int? = message.messageId): Message =
        ctx.replyToMessageWithPhoto()
            .setFile(InputFile(url))
            .setCaption(caption)
            .setReplyToMessageId(messageId)
            .setParseMode(ParseMode.HTML)
            .call(ctx.sender)

    protected fun sendVoice(name: String, inputStream: InputStream, messageId: Int): Message =
        ctx.replyToMessageWithAudio()
            .setFile(name, inputStream)
            .setReplyToMessageId(messageId)
            .call(ctx.sender)

    protected fun errorReplyWithTextDocument(text: String, caption: String): Int =
        ctx.replyWithDocument()
            .setFile("$userId-${message.messageId}.txt", text.byteInputStream())
            .setCaption(caption)
            .setReplyToMessageId(message.messageId)
            .call(ctx.sender)
            .messageId

    private fun chunkedReplyToMessage(text: String, messageId: Int, webPagePreview: Boolean, parseMode: String) {
        var messageIdToReply = messageId

        text.chunked(MAX_MESSAGE_LENGTH).forEach {
            if (it.isNotBlank()) {
                messageIdToReply =
                    ctx.replyToMessage(it)
                        .setReplyToMessageId(messageIdToReply)
                        .setParseMode(parseMode)
                        .setWebPagePreviewEnabled(webPagePreview)
                        .call(ctx.sender)
                        .messageId
            }
        }
    }
}
