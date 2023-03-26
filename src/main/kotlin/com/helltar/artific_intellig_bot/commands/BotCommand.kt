package com.helltar.artific_intellig_bot.commands

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.helltar.artific_intellig_bot.BotConfig
import com.helltar.artific_intellig_bot.DIR_DB
import com.helltar.artific_intellig_bot.EXT_DISABLED
import com.helltar.artific_intellig_bot.db.Database
import org.json.simple.JSONValue
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import java.io.File

abstract class BotCommand(val ctx: MessageContext, val args: List<String> = listOf()) : BotConfig() {

    protected val userId = ctx.user().id

    abstract fun run()

    fun isCommandDisabled(commandName: String) =
        File(DIR_DB + commandName + EXT_DISABLED).exists()

    fun isChatInWhiteList() =
        Database.chatWhiteList.isChatExists(ctx.chatId())

    fun isUserBanned(userId: Long) =
        Database.banList.isUserBanned(userId)

    fun isNotAdmin() =
        !Database.sudoers.isAdmin(userId)

    fun isAdmin() =
        !isNotAdmin()

    fun isCreator(userId: Long = this.userId) =
        Database.sudoers.isCreator(userId)

    fun replyToMessage(text: String, messageId: Int = ctx.messageId(), enableWebPagePreview: Boolean = false, markdown: Boolean = false): Int =
        ctx.replyToMessage(text)
            .setReplyToMessageId(messageId)
            .setParseMode(if (!markdown) ParseMode.HTML else ParseMode.MARKDOWN)
            .setWebPagePreviewEnabled(enableWebPagePreview)
            .call(ctx.sender)
            .messageId

    protected fun replyToMessageWithPhoto(file: File, caption: String): Message =
        ctx.replyToMessageWithPhoto()
            .setFile(file)
            .setCaption(caption)
            .setParseMode(ParseMode.HTML)
            .call(ctx.sender)

    protected fun replyToMessageWithPhoto(url: String, caption: String = "", messageId: Int = ctx.messageId()): Message =
        ctx.replyToMessageWithPhoto()
            .setFile(InputFile(url))
            .setCaption(caption)
            .setReplyToMessageId(messageId)
            .setParseMode(ParseMode.HTML)
            .call(ctx.sender)

    protected fun sendVoice(file: File,messageId: Int = ctx.messageId()): Message =
        ctx.replyToMessageWithAudio()
            .setFile(file)
            .setReplyToMessageId(messageId)
            .call(ctx.sender)

    protected fun deleteMessage(messageId: Int) =
        ctx.deleteMessage().setMessageId(messageId).callAsync(ctx.sender)

    protected data class ReqData(
        val url: String,
        val apiKey: String,
        val json: String,
        val prompt: String,
        val additionHeader: Map<String, String> = mapOf()
    )

    protected fun sendPrompt(reqData: ReqData) =
        reqData.run {
            url.httpPost()
                .header("Content-Type", "application/json")
                .header("Authorization", apiKey)
                .header(additionHeader)
                .timeout(60000)
                .timeoutRead(60000)
                .jsonBody(String.format(json, JSONValue.escape(prompt)))
                .responseString()
        }
}
