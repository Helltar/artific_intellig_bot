package com.helltar.aibot.bot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.BotModuleOptions
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.SimpleCommand
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Config.creatorId
import com.helltar.aibot.Config.telegramBotUsername
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands.CMD_ABOUT
import com.helltar.aibot.commands.Commands.CMD_ADD_ADMIN
import com.helltar.aibot.commands.Commands.CMD_ADD_CHAT
import com.helltar.aibot.commands.Commands.CMD_ADMIN_LIST
import com.helltar.aibot.commands.Commands.CMD_ASR
import com.helltar.aibot.commands.Commands.CMD_BAN_LIST
import com.helltar.aibot.commands.Commands.CMD_BAN_USER
import com.helltar.aibot.commands.Commands.CMD_CHAT
import com.helltar.aibot.commands.Commands.CMD_CHATCTX
import com.helltar.aibot.commands.Commands.CMD_CHATS_WHITE_LIST
import com.helltar.aibot.commands.Commands.CMD_CHAT_CTX_REMOVE
import com.helltar.aibot.commands.Commands.CMD_DALLE
import com.helltar.aibot.commands.Commands.CMD_DALLE_VARIATIONS
import com.helltar.aibot.commands.Commands.CMD_DISABLE
import com.helltar.aibot.commands.Commands.CMD_ENABLE
import com.helltar.aibot.commands.Commands.CMD_GLOBAL_SLOW_MODE
import com.helltar.aibot.commands.Commands.CMD_GPT_VISION
import com.helltar.aibot.commands.Commands.CMD_MYID
import com.helltar.aibot.commands.Commands.CMD_PRIVACY
import com.helltar.aibot.commands.Commands.CMD_RM_ADMIN
import com.helltar.aibot.commands.Commands.CMD_RM_CHAT
import com.helltar.aibot.commands.Commands.CMD_SLOW_MODE
import com.helltar.aibot.commands.Commands.CMD_SLOW_MODE_LIST
import com.helltar.aibot.commands.Commands.CMD_SLOW_MODE_OFF
import com.helltar.aibot.commands.Commands.CMD_START
import com.helltar.aibot.commands.Commands.CMD_UNBAN_USER
import com.helltar.aibot.commands.Commands.CMD_UPDATE_API_KEY
import com.helltar.aibot.commands.Commands.CMD_UPDATE_PRIVACY_POLICY
import com.helltar.aibot.commands.admin.ban.*
import com.helltar.aibot.commands.admin.chat.*
import com.helltar.aibot.commands.admin.slowmode.*
import com.helltar.aibot.commands.admin.sudoers.*
import com.helltar.aibot.commands.admin.system.*
import com.helltar.aibot.commands.user.*
import com.helltar.aibot.commands.user.audio.AsrWhisper
import com.helltar.aibot.commands.user.chat.*
import com.helltar.aibot.commands.user.images.*
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
        registerSimpleCommand(CMD_PRIVACY, ::Privacy)
        registerSimpleCommand(CMD_CHATCTX, ::ChatCtx, true)
        registerSimpleCommand(CMD_CHAT_CTX_REMOVE, ::ChatCtxRemove, true)

        registerLongRunningCommand(CMD_CHAT, ::ChatGPT)
        registerLongRunningCommand(CMD_GPT_VISION, ::Vision)
        registerLongRunningCommand(CMD_DALLE, ::DallEGenerations)
        registerLongRunningCommand(CMD_DALLE_VARIATIONS, ::DallEVariations)
        registerLongRunningCommand(CMD_ASR, ::AsrWhisper)

        registerAdminCommand(CMD_ENABLE, { CommandsState(it) })
        registerAdminCommand(CMD_DISABLE, { CommandsState(it, true) })
        registerAdminCommand(CMD_BAN_LIST, ::Banlist)
        registerAdminCommand(CMD_BAN_USER, ::BanUser)
        registerAdminCommand(CMD_UNBAN_USER, ::UnbanUser)
        registerAdminCommand(CMD_SLOW_MODE, ::Slowmode)
        registerAdminCommand(CMD_SLOW_MODE_OFF, ::SlowmodeOff)
        registerAdminCommand(CMD_SLOW_MODE_LIST, ::SlowmodeList)
        registerAdminCommand(CMD_RM_ADMIN, ::RemoveAdmin)
        registerAdminCommand(CMD_ADMIN_LIST, ::AdminList, true)
        registerAdminCommand(CMD_CHATS_WHITE_LIST, ::ChatsWhitelist, true)
        registerAdminCommand(CMD_RM_CHAT, ::RemoveChat)

        registerCreatorCommand(CMD_ADD_ADMIN, ::AddAdmin)
        registerCreatorCommand(CMD_ADD_CHAT, ::AddChat)
        registerCreatorCommand(CMD_GLOBAL_SLOW_MODE, ::GlobalSlowmode)
        registerCreatorCommand(CMD_UPDATE_API_KEY, ::UpdateApiKey, true)
        registerCreatorCommand(CMD_UPDATE_PRIVACY_POLICY, ::UpdatePrivacyPolicy, true)
    }

    override fun onUpdate(update: Update): BotApiMethod<*>? {

        fun executeChatGPT(ctx: MessageContext) =
            commandExecutor.execute(ChatGPT(ctx), createCommandOptions(isLongRunningCommand = true))

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
