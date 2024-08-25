package com.helltar.aibot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.EnvConfig.creatorId
import com.helltar.aibot.EnvConfig.telegramBotUsername
import com.helltar.aibot.db.dao.*
import kotlinx.serialization.json.Json
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.message.Message
import java.io.File
import java.io.InputStream
import java.util.concurrent.CompletableFuture

abstract class BotCommand(val ctx: MessageContext) {

    protected companion object {
        const val PROVIDER_OPENAI_COM = "openai.com"
        const val PROVIDER_STABILITY_AI = "stability.ai"
        const val PROVIDER_MICROSOFT = "microsoft.com"
    }

    val arguments: Array<String> = ctx.arguments()
    val userLanguageCode = ctx.user().languageCode ?: "en"

    protected val userId = ctx.user().id
    protected val message = ctx.message()
    protected val replyMessage: Message? = message.replyToMessage
    protected val isReply = message.isReply && message.replyToMessage.messageId != message.replyToMessage.messageThreadId // todo: tmp. fix, check.
    protected val isNotReply = !isReply
    protected val argumentsString: String = ctx.argumentsAsString()

    protected val json = Json { ignoreUnknownKeys = true; encodeDefaults = true; explicitNulls = false }

    abstract suspend fun run()
    abstract fun getCommandName(): String

    suspend fun isCommandDisabled(command: String) =
        commandsDao.isDisabled(command)

    suspend fun isChatInWhiteList() =
        chatWhitelistDao.isChatExists(ctx.chatId())

    suspend fun isUserBanned(userId: Long) =
        banlistDao.isUserBanned(userId)

    suspend fun isAdmin() =
        sudoersDao.isAdmin(userId)

    fun isCreator(userId: Long = this.userId) =
        userId == creatorId

    fun isNotMe(username: String?) =
        username != telegramBotUsername

    fun replyToMessage(
        text: String,
        messageId: Int = message.messageId,
        enableWebPagePreview: Boolean = false,
        markdown: Boolean = false
    ): Int =
        ctx.replyToMessage(text)
            .setReplyToMessageId(if (replyMessage?.from?.isBot == false) messageId else message.messageId) // todo: refact.
            .setParseMode(if (!markdown) ParseMode.HTML else ParseMode.MARKDOWN)
            .setWebPagePreviewEnabled(enableWebPagePreview)
            .call(ctx.sender)
            .messageId

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

    protected fun reply(text: String) {
        ctx.reply()
            .setText(text)
            .setParseMode(ParseMode.HTML)
            .call(ctx.sender)
    }

    protected suspend fun getApiKey(provider: String) =
        apiKeysDao.getKey(provider)

    protected fun replyToMessageWithPhoto(file: File, caption: String = "", messageId: Int = message.messageId): Message =
        ctx.replyToMessageWithPhoto()
            .setFile(file)
            .setCaption(caption)
            .setReplyToMessageId(messageId)
            .setParseMode(ParseMode.HTML)
            .call(ctx.sender)

    protected fun replyToMessageWithPhoto(url: String, caption: String = "", messageId: Int = message.messageId): Message =
        ctx.replyToMessageWithPhoto()
            .setFile(InputFile(url))
            .setCaption(caption)
            .setReplyToMessageId(messageId)
            .setParseMode(ParseMode.HTML)
            .call(ctx.sender)

    protected fun replyToMessageWithDocument(file: File, caption: String): Int =
        ctx.replyWithDocument()
            .setFile(file)
            .setCaption(caption)
            .setReplyToMessageId(message.messageId)
            .call(ctx.sender)
            .messageId

    protected fun sendVoice(name: String, inputStream: InputStream, messageId: Int): Message =
        ctx.replyToMessageWithAudio()
            .setFile(name, inputStream)
            .setReplyToMessageId(messageId)
            .call(ctx.sender)

    protected fun errorReplyToMessageWithTextDocument(text: String, caption: String): Int =
        ctx.replyWithDocument()
            .setFile("$userId-${message.messageId}.txt", text.byteInputStream())
            .setCaption(caption)
            .setReplyToMessageId(message.messageId)
            .call(ctx.sender)
            .messageId

    protected suspend fun getOpenAIAuthHeader() =
        mapOf("Authorization" to "Bearer ${getApiKey(PROVIDER_OPENAI_COM)}")

    protected suspend fun getOpenAIHeaders() =
        mapOf("Content-Type" to "application/json") + getOpenAIAuthHeader()
}