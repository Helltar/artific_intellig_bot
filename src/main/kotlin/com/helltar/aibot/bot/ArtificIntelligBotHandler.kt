package com.helltar.aibot.bot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.BotModuleOptions
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.SimpleCommand
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.EnvConfig.creatorId
import com.helltar.aibot.EnvConfig.telegramBotUsername
import com.helltar.aibot.commands.CommandExecutor
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
import com.helltar.aibot.commands.Commands.CMD_GPT_VISION
import com.helltar.aibot.commands.Commands.CMD_MYID
import com.helltar.aibot.commands.Commands.CMD_RM_ADMIN
import com.helltar.aibot.commands.Commands.CMD_RM_CHAT
import com.helltar.aibot.commands.Commands.CMD_SDIFF
import com.helltar.aibot.commands.Commands.CMD_SLOW_MODE
import com.helltar.aibot.commands.Commands.CMD_SLOW_MODE_LIST
import com.helltar.aibot.commands.Commands.CMD_SLOW_MODE_OFF
import com.helltar.aibot.commands.Commands.CMD_START
import com.helltar.aibot.commands.Commands.CMD_UNBAN_USER
import com.helltar.aibot.commands.Commands.CMD_UPDATE_API_KEY
import com.helltar.aibot.commands.admin.ban.BanUser
import com.helltar.aibot.commands.admin.ban.UnbanUser
import com.helltar.aibot.commands.admin.chat.AddChat
import com.helltar.aibot.commands.admin.chat.ChatsWhitelist
import com.helltar.aibot.commands.admin.chat.RemoveChat
import com.helltar.aibot.commands.admin.slowmode.Slowmode
import com.helltar.aibot.commands.admin.slowmode.SlowmodeOff
import com.helltar.aibot.commands.admin.sudoers.AddAdmin
import com.helltar.aibot.commands.admin.sudoers.AdminList
import com.helltar.aibot.commands.admin.sudoers.RemoveAdmin
import com.helltar.aibot.commands.admin.system.CommandsState
import com.helltar.aibot.commands.admin.system.UpdateApiKey
import com.helltar.aibot.commands.user.About
import com.helltar.aibot.commands.user.MyId
import com.helltar.aibot.commands.user.Start
import com.helltar.aibot.commands.user.audio.AsrWhisper
import com.helltar.aibot.commands.user.chat.ChatCtx
import com.helltar.aibot.commands.user.chat.ChatCtxRemove
import com.helltar.aibot.commands.user.chat.ChatGPT
import com.helltar.aibot.commands.user.images.DallE2
import com.helltar.aibot.commands.user.images.DalleVariations
import com.helltar.aibot.commands.user.images.GPT4Vision
import com.helltar.aibot.commands.user.images.StableDiffusion
import com.helltar.aibot.commands.user.lists.Banlist
import com.helltar.aibot.commands.user.lists.SlowmodeList
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.function.Consumer

class ArtificIntelligBotHandler(botModuleOptions: BotModuleOptions) : BotHandler(botModuleOptions) {

    private val authority = SimpleAuthority(creatorId)
    private val registry = CommandRegistry(telegramBotUsername, authority)

    private val commandExecutor = CommandExecutor()

    init {
        registry.run {
            register(simpleCommand(CMD_START) { commandExecutor.execute(Start(it), checkRights = false) })
            register(simpleCommand(CMD_MYID) { commandExecutor.execute(MyId(it), checkRights = false) })
            register(simpleCommand(CMD_ABOUT) { commandExecutor.execute(About(it), checkRights = false) })

            register(simpleCommand(CMD_CHAT) { commandExecutor.execute(ChatGPT(it), isLongtimeCommand = true) })
            register(simpleCommand(CMD_GPT_VISION) { commandExecutor.execute(GPT4Vision(it), isLongtimeCommand = true) })
            register(simpleCommand(CMD_CHATCTX) { commandExecutor.execute(ChatCtx(it)) })
            register(simpleCommand(CMD_CHAT_CTX_REMOVE) { commandExecutor.execute(ChatCtxRemove(it)) })

            register(simpleCommand(CMD_DALLE) { commandExecutor.execute(DallE2(it), isLongtimeCommand = true) })
            register(simpleCommand(CMD_DALLE_VARIATIONS) { commandExecutor.execute(DalleVariations(it), isLongtimeCommand = true) })
            register(simpleCommand(CMD_SDIFF) { commandExecutor.execute(StableDiffusion(it), isLongtimeCommand = true) })
            register(simpleCommand(CMD_ASR) { commandExecutor.execute(AsrWhisper(it), isLongtimeCommand = true) })
            register(simpleCommand(CMD_BAN_LIST) { commandExecutor.execute(Banlist(it)) })
            register(simpleCommand(CMD_SLOW_MODE_LIST) { commandExecutor.execute(SlowmodeList(it)) })

            register(simpleCommand(CMD_ENABLE) { commandExecutor.execute(CommandsState(it), true) })
            register(simpleCommand(CMD_DISABLE) { commandExecutor.execute(CommandsState(it, true), true) })
            register(simpleCommand(CMD_BAN_USER) { commandExecutor.execute(BanUser(it), true) })
            register(simpleCommand(CMD_UNBAN_USER) { commandExecutor.execute(UnbanUser(it), true) })
            register(simpleCommand(CMD_SLOW_MODE) { commandExecutor.execute(Slowmode(it), true) })
            register(simpleCommand(CMD_SLOW_MODE_OFF) { commandExecutor.execute(SlowmodeOff(it), true) })

            register(simpleCommand(CMD_ADD_ADMIN) { commandExecutor.execute(AddAdmin(it), isCreatorCommand = true) })
            register(simpleCommand(CMD_RM_ADMIN) { commandExecutor.execute(RemoveAdmin(it), true) })
            register(simpleCommand(CMD_ADMIN_LIST) { commandExecutor.execute(AdminList(it), true, userChatOnly = true) })

            register(simpleCommand(CMD_CHATS_WHITE_LIST) { commandExecutor.execute(ChatsWhitelist(it), true, userChatOnly = true) })
            register(simpleCommand(CMD_ADD_CHAT) { commandExecutor.execute(AddChat(it), isCreatorCommand = true) })
            register(simpleCommand(CMD_RM_CHAT) { commandExecutor.execute(RemoveChat(it), true) })

            register(simpleCommand(CMD_UPDATE_API_KEY) { commandExecutor.execute(UpdateApiKey(it), userChatOnly = true, isCreatorCommand = true) })
        }
    }

    override fun onUpdate(update: Update): BotApiMethod<*>? {
        fun Message.hasMentions() =
            this.entities.any { it.type == EntityType.MENTION || it.type == EntityType.TEXTMENTION }

        fun chatGPT(ctx: MessageContext) =
            commandExecutor.execute(ChatGPT(ctx), isLongtimeCommand = true)

        if (update.hasMessage() && update.message.isReply && update.message.hasText()) {
            val message = update.message
            val replyToMessage = message.replyToMessage
            val text = message.text
            val isMe = replyToMessage.from.userName == telegramBotUsername

            if (isMe && !replyToMessage.hasPhoto() && !text.startsWith("/")) {
                val ctx = MessageContext(this, update, "")

                if (!message.hasEntities()) // if it doesn't have text formatting, @username, etc...
                    chatGPT(ctx)
                else
                    if (!message.hasMentions()) // if it has text formatting, etc..., but doesn't have @username
                        chatGPT(ctx)
            }
        }

        registry.handleUpdate(this, update)

        return null
    }

    override fun handleTelegramApiException(tae: TelegramApiException?) {
        throw tae ?: TelegramApiException("TelegramApiException") // todo: TelegramApiException
    }

    private fun simpleCommand(command: String, consumer: Consumer<MessageContext>) =
        SimpleCommand("/$command", consumer)
}