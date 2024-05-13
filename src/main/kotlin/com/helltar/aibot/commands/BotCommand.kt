package com.helltar.aibot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.BotConfig
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.ApiKeyType
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import java.io.File

abstract class BotCommand(val ctx: MessageContext) {

    private companion object {
        const val DEFAULT_LANG_CODE = "en"
    }

    val args: Array<String> = ctx.arguments()
    val userLanguageCode = ctx.user().languageCode ?: DEFAULT_LANG_CODE

    protected val userId = ctx.user().id
    protected val message = ctx.message()
    protected val replyMessage: Message? = message.replyToMessage
    protected val isReply = message.isReply && message.replyToMessage.messageId != message.replyToMessage.messageThreadId // todo: tmp. fix, check.
    protected val isNotReply = !isReply
    protected val argsText: String = ctx.argumentsAsString()

    abstract fun run()
    abstract fun getCommandName(): String

    fun isCommandDisabled(command: String) =
        DatabaseFactory.commandsState.isDisabled(command)

    fun isChatInWhiteList() =
        DatabaseFactory.chatWhiteList.isChatExists(ctx.chatId())

    fun isUserBanned(userId: Long) =
        DatabaseFactory.banList.isUserBanned(userId)

    fun isAdmin() =
        DatabaseFactory.sudoers.isAdmin(userId)

    fun isCreator(userId: Long = this.userId) =
        DatabaseFactory.sudoers.isCreator(userId)

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

    fun deleteMessage(messageId: Int) =
        ctx.deleteMessage().setMessageId(messageId).callAsync(ctx.sender)

    // todo: getLoadingGifFileId
    fun getLoadingGifFileId() =
        DatabaseFactory.filesIds.getFileId(BotConfig.FILE_LOADING_GIF)
            ?: run {
                val message = sendDocument(File("${BotConfig.DIR_FILES}/${BotConfig.FILE_LOADING_GIF}"))
                val fileId = message.document.fileId
                deleteMessage(message.messageId)

                DatabaseFactory.filesIds.add(BotConfig.FILE_LOADING_GIF, fileId)

                return fileId
            }

    protected fun reply(text: String) {
        ctx.reply()
            .setText(text)
            .setParseMode(ParseMode.HTML)
            .call(ctx.sender)
    }

    protected fun getApiKey(provider: String): String? {
        val apiKeyType = when {
            isCreator() -> ApiKeyType.CREATOR
            isAdmin() -> ApiKeyType.ADMIN
            else -> ApiKeyType.USER
        }

        return DatabaseFactory.apiKeys.getApiKey(provider, apiKeyType) ?: DatabaseFactory.apiKeys.getApiKey(provider, ApiKeyType.USER) // todo: getApiKey
    }

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

    protected fun sendVoice(file: File, messageId: Int): Message =
        ctx.replyToMessageWithAudio()
            .setFile(file)
            .setReplyToMessageId(messageId)
            .call(ctx.sender)

    private fun sendDocument(file: File): Message =
        ctx.replyWithDocument()
            .setFile(file)
            .call(ctx.sender)
}