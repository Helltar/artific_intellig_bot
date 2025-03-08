package com.helltar.aibot.bot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.BotModuleOptions
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.SimpleCommand
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands.Admin.CMD_ADMIN_LIST
import com.helltar.aibot.commands.Commands.Admin.CMD_BAN_LIST
import com.helltar.aibot.commands.Commands.Admin.CMD_BAN_USER
import com.helltar.aibot.commands.Commands.Admin.CMD_CHATS_WHITE_LIST
import com.helltar.aibot.commands.Commands.Admin.CMD_DISABLE
import com.helltar.aibot.commands.Commands.Admin.CMD_ENABLE
import com.helltar.aibot.commands.Commands.Admin.CMD_RM_ADMIN
import com.helltar.aibot.commands.Commands.Admin.CMD_RM_CHAT
import com.helltar.aibot.commands.Commands.Admin.CMD_SLOW_MODE
import com.helltar.aibot.commands.Commands.Admin.CMD_SLOW_MODE_LIST
import com.helltar.aibot.commands.Commands.Admin.CMD_SLOW_MODE_OFF
import com.helltar.aibot.commands.Commands.Admin.CMD_UNBAN_USER
import com.helltar.aibot.commands.Commands.Creator.CMD_ADD_ADMIN
import com.helltar.aibot.commands.Commands.Creator.CMD_ADD_CHAT
import com.helltar.aibot.commands.Commands.Creator.CMD_DEEP_SEEK_OFF
import com.helltar.aibot.commands.Commands.Creator.CMD_DEEP_SEEK_ON
import com.helltar.aibot.commands.Commands.Creator.CMD_GLOBAL_SLOW_MODE
import com.helltar.aibot.commands.Commands.Creator.CMD_UPDATE_API_KEY
import com.helltar.aibot.commands.Commands.Simple.CMD_ABOUT
import com.helltar.aibot.commands.Commands.Simple.CMD_MYID
import com.helltar.aibot.commands.Commands.Simple.CMD_START
import com.helltar.aibot.commands.Commands.User.CMD_ASR
import com.helltar.aibot.commands.Commands.User.CMD_CHAT
import com.helltar.aibot.commands.Commands.User.CMD_CHATCTX
import com.helltar.aibot.commands.Commands.User.CMD_CHAT_CTX_REMOVE
import com.helltar.aibot.commands.Commands.User.CMD_DALLE
import com.helltar.aibot.commands.Commands.User.CMD_DALLE_VARIATIONS
import com.helltar.aibot.commands.Commands.User.CMD_GPT_VISION
import com.helltar.aibot.commands.admin.ban.BanUser
import com.helltar.aibot.commands.admin.ban.Banlist
import com.helltar.aibot.commands.admin.ban.UnbanUser
import com.helltar.aibot.commands.admin.chat.AddChat
import com.helltar.aibot.commands.admin.chat.ChatsWhitelist
import com.helltar.aibot.commands.admin.chat.RemoveChat
import com.helltar.aibot.commands.admin.slowmode.Slowmode
import com.helltar.aibot.commands.admin.slowmode.SlowmodeList
import com.helltar.aibot.commands.admin.slowmode.SlowmodeOff
import com.helltar.aibot.commands.admin.sudoers.AddAdmin
import com.helltar.aibot.commands.admin.sudoers.AdminList
import com.helltar.aibot.commands.admin.sudoers.RemoveAdmin
import com.helltar.aibot.commands.admin.system.CommandState
import com.helltar.aibot.commands.admin.system.DeepSeekState
import com.helltar.aibot.commands.admin.system.GlobalSlowmode
import com.helltar.aibot.commands.admin.system.UpdateApiKey
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.commands.simple.About
import com.helltar.aibot.commands.simple.MyId
import com.helltar.aibot.commands.simple.Start
import com.helltar.aibot.commands.user.audio.Transcription
import com.helltar.aibot.commands.user.chat.Chat
import com.helltar.aibot.commands.user.chat.ChatCtx
import com.helltar.aibot.commands.user.chat.ChatCtxRemove
import com.helltar.aibot.commands.user.image.DallEGenerations
import com.helltar.aibot.commands.user.image.DallEVariations
import com.helltar.aibot.commands.user.image.Vision
import com.helltar.aibot.config.Config.creatorId
import com.helltar.aibot.config.Config.telegramBotUsername
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class ArtificIntelligBotHandler(botModuleOptions: BotModuleOptions) : BotHandler(botModuleOptions) {

    private val authority = SimpleAuthority(creatorId)
    private val registry = CommandRegistry(telegramBotUsername, authority)

    private val commandExecutor = CommandExecutor()

    init {
        registerSimpleCommand(CMD_START, ::Start)
        registerSimpleCommand(CMD_MYID, ::MyId)
        registerSimpleCommand(CMD_ABOUT, ::About)
        registerSimpleCommand(CMD_CHATCTX, ::ChatCtx, checkRights = true)
        registerSimpleCommand(CMD_CHAT_CTX_REMOVE, ::ChatCtxRemove, checkRights = true)

        registerLongRunningCommand(CMD_CHAT, ::Chat)
        registerLongRunningCommand(CMD_GPT_VISION, ::Vision)
        registerLongRunningCommand(CMD_DALLE, ::DallEGenerations)
        registerLongRunningCommand(CMD_DALLE_VARIATIONS, ::DallEVariations)
        registerLongRunningCommand(CMD_ASR, ::Transcription)

        registerAdminCommand(CMD_ENABLE, ::CommandState)
        registerAdminCommand(CMD_DISABLE, { CommandState(it, true) })
        registerAdminCommand(CMD_BAN_LIST, ::Banlist)
        registerAdminCommand(CMD_BAN_USER, ::BanUser)
        registerAdminCommand(CMD_UNBAN_USER, ::UnbanUser)
        registerAdminCommand(CMD_SLOW_MODE, ::Slowmode)
        registerAdminCommand(CMD_SLOW_MODE_OFF, ::SlowmodeOff)
        registerAdminCommand(CMD_SLOW_MODE_LIST, ::SlowmodeList)
        registerAdminCommand(CMD_RM_ADMIN, ::RemoveAdmin)
        registerAdminCommand(CMD_RM_CHAT, ::RemoveChat)
        registerAdminCommand(CMD_ADMIN_LIST, ::AdminList, privateChatOnly = true)
        registerAdminCommand(CMD_CHATS_WHITE_LIST, ::ChatsWhitelist, privateChatOnly = true)

        registerCreatorCommand(CMD_ADD_ADMIN, ::AddAdmin)
        registerCreatorCommand(CMD_ADD_CHAT, ::AddChat)
        registerCreatorCommand(CMD_GLOBAL_SLOW_MODE, ::GlobalSlowmode)
        registerCreatorCommand(CMD_UPDATE_API_KEY, ::UpdateApiKey, privateChatOnly = true)
        registerCreatorCommand(CMD_DEEP_SEEK_ON, { DeepSeekState(it, true) }, privateChatOnly = true)
        registerCreatorCommand(CMD_DEEP_SEEK_OFF, ::DeepSeekState, privateChatOnly = true)
    }

    override fun onUpdate(update: Update): BotApiMethod<*>? {

        fun executeChatGPT(ctx: MessageContext) =
            commandExecutor.execute(Chat(ctx), createCommandOptions(isLongRunningCommand = true))

        fun shouldProcessMessage(replyToMessage: Message, text: String): Boolean {
            val isMe = replyToMessage.from.userName == telegramBotUsername
            return isMe && !replyToMessage.hasPhoto() && !text.startsWith("/")
        }

        fun processMessage(message: Message) {
            val ctx = MessageContext(this, update, "")

            if (!message.hasEntities() || !message.entities.any { it.type == EntityType.MENTION || it.type == EntityType.TEXTMENTION })
                executeChatGPT(ctx)
        }

        if (update.hasMessage() && update.message.isReply && update.message.hasText()) {
            val message = update.message

            if (shouldProcessMessage(message.replyToMessage, message.text))
                processMessage(message)
        }

        registry.handleUpdate(this, update)

        return null
    }

    override fun handleTelegramApiException(tae: TelegramApiException?) {
        throw tae ?: TelegramApiException("TelegramApiException") // todo: TelegramApiException
    }

    private fun createCommandOptions(
        checkRights: Boolean = true,
        isAdminCommand: Boolean = false,
        isCreatorCommand: Boolean = false,
        isLongRunningCommand: Boolean = false,
        privateChatOnly: Boolean = false
    ) =
        CommandExecutor.CommandOptions(checkRights, isAdminCommand, isCreatorCommand, isLongRunningCommand, privateChatOnly)

    private fun registerCommand(command: String, botCommand: (MessageContext) -> BotCommand, options: CommandExecutor.CommandOptions) {
        registry.register(SimpleCommand("/$command") { commandExecutor.execute(botCommand(it), options) })
    }

    private fun registerSimpleCommand(command: String, botCommand: (MessageContext) -> BotCommand, checkRights: Boolean = false) {
        registerCommand(command, botCommand, createCommandOptions(checkRights = checkRights))
    }

    private fun registerLongRunningCommand(command: String, botCommand: (MessageContext) -> BotCommand) {
        registerCommand(command, botCommand, createCommandOptions(isLongRunningCommand = true))
    }

    private fun registerAdminCommand(command: String, botCommand: (MessageContext) -> BotCommand, privateChatOnly: Boolean = false) {
        registerCommand(command, botCommand, createCommandOptions(isAdminCommand = true, privateChatOnly = privateChatOnly))
    }

    private fun registerCreatorCommand(command: String, botCommand: (MessageContext) -> BotCommand, privateChatOnly: Boolean = false) {
        registerCommand(command, botCommand, createCommandOptions(isCreatorCommand = true, privateChatOnly = privateChatOnly))
    }
}
