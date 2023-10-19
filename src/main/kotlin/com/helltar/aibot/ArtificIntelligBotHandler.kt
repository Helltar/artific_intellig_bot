package com.helltar.aibot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.SimpleCommand
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority
import com.annimon.tgbotsmodule.commands.context.MessageContext
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
import com.helltar.aibot.commands.Commands.CMD_DISABLE
import com.helltar.aibot.commands.Commands.CMD_ENABLE
import com.helltar.aibot.commands.Commands.CMD_MYID
import com.helltar.aibot.commands.Commands.CMD_RM_ADMIN
import com.helltar.aibot.commands.Commands.CMD_RM_CHAT
import com.helltar.aibot.commands.Commands.CMD_SDIFF
import com.helltar.aibot.commands.Commands.CMD_SLOW_MODE
import com.helltar.aibot.commands.Commands.CMD_SLOW_MODE_LIST
import com.helltar.aibot.commands.Commands.CMD_SLOW_MODE_OFF
import com.helltar.aibot.commands.Commands.CMD_START
import com.helltar.aibot.commands.Commands.CMD_UNBAN_USER
import com.helltar.aibot.commands.Commands.CMD_UPTIME
import com.helltar.aibot.commands.admin.admin.AddAdmin
import com.helltar.aibot.commands.admin.admin.AdminList
import com.helltar.aibot.commands.admin.admin.RemoveAdmin
import com.helltar.aibot.commands.admin.ban.BanUser
import com.helltar.aibot.commands.admin.ban.UnbanUser
import com.helltar.aibot.commands.admin.chat.AddChat
import com.helltar.aibot.commands.admin.chat.ChatsWhiteList
import com.helltar.aibot.commands.admin.chat.RemoveChat
import com.helltar.aibot.commands.admin.command.ChangeState
import com.helltar.aibot.commands.admin.slowmode.SlowMode
import com.helltar.aibot.commands.admin.slowmode.SlowModeOff
import com.helltar.aibot.commands.user.About
import com.helltar.aibot.commands.user.MyId
import com.helltar.aibot.commands.user.Start
import com.helltar.aibot.commands.user.Uptime
import com.helltar.aibot.commands.user.audio.AsrWhisper
import com.helltar.aibot.commands.user.chat.ChatCtx
import com.helltar.aibot.commands.user.chat.ChatCtxRemove
import com.helltar.aibot.commands.user.chat.ChatGPT
import com.helltar.aibot.commands.user.images.DallE2
import com.helltar.aibot.commands.user.images.DalleVariations
import com.helltar.aibot.commands.user.images.StableDiffusion
import com.helltar.aibot.commands.user.lists.BanList
import com.helltar.aibot.commands.user.lists.SlowModeList
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.function.Consumer

class ArtificIntelligBotHandler(private val botConfig: BotConfig.JsonData) : BotHandler(botConfig.token) {

    private val authority = SimpleAuthority(botConfig.creatorId)
    private val commands = CommandRegistry(botConfig.username, authority)

    private val ce = CommandExecutor(botConfig)

    init {
        commands.run {
            register(simpleCommand(CMD_START) { ce.execute(Start(it), checkRights = false) })
            register(simpleCommand(CMD_MYID) { ce.execute(MyId(it), checkRights = false) })
            register(simpleCommand(CMD_ABOUT) { ce.execute(About(it), checkRights = false) })

            register(simpleCommand(CMD_CHAT) { ce.execute(ChatGPT(it), isLongtimeCommand = true) })
            register(simpleCommand(CMD_CHATCTX) { ce.execute(ChatCtx(it)) })
            register(simpleCommand(CMD_CHAT_CTX_REMOVE) { ce.execute(ChatCtxRemove(it)) })

            register(simpleCommand(CMD_DALLE) { ce.execute(DallE2(it), isLongtimeCommand = true) })
            register(simpleCommand(CMD_SDIFF) { ce.execute(StableDiffusion(it), isLongtimeCommand = true) })
            register(simpleCommand(CMD_ASR) { ce.execute(AsrWhisper(it), isLongtimeCommand = true) })
            register(simpleCommand(CMD_BAN_LIST) { ce.execute(BanList(it)) })
            register(simpleCommand(CMD_UPTIME) { ce.execute(Uptime(it)) })
            register(simpleCommand(CMD_SLOW_MODE_LIST) { ce.execute(SlowModeList(it)) })

            register(simpleCommand(CMD_ENABLE) { ce.execute(ChangeState(it), true) })
            register(simpleCommand(CMD_DISABLE) { ce.execute(ChangeState(it, true), true) })
            register(simpleCommand(CMD_BAN_USER) { ce.execute(BanUser(it), true) })
            register(simpleCommand(CMD_UNBAN_USER) { ce.execute(UnbanUser(it), true) })
            register(simpleCommand(CMD_SLOW_MODE) { ce.execute(SlowMode(it), true) })
            register(simpleCommand(CMD_SLOW_MODE_OFF) { ce.execute(SlowModeOff(it), true) })

            register(simpleCommand(CMD_ADD_ADMIN) { ce.execute(AddAdmin(it), isCreatorCommand = true) })
            register(simpleCommand(CMD_RM_ADMIN) { ce.execute(RemoveAdmin(it), true) })
            register(simpleCommand(CMD_ADMIN_LIST) { ce.execute(AdminList(it), true, userChatOnly = true) })

            register(simpleCommand(CMD_CHATS_WHITE_LIST) { ce.execute(ChatsWhiteList(it), true, userChatOnly = true) })
            register(simpleCommand(CMD_ADD_CHAT) { ce.execute(AddChat(it), isCreatorCommand = true) })
            register(simpleCommand(CMD_RM_CHAT) { ce.execute(RemoveChat(it), true) })
        }
    }

    private fun simpleCommand(command: String, c: Consumer<MessageContext>) =
        SimpleCommand("/$command", c)

    override fun getBotUsername() =
        botConfig.username

    override fun handleTelegramApiException(ex: TelegramApiException?) {
        throw ex ?: TelegramApiException("TelegramApiException") // todo: TelegramApiException
    }

    override fun onUpdate(update: Update): BotApiMethod<*>? {

        /* todo: refact */

        fun hasMentions(message: Message) =
            message.entities.stream().anyMatch { e -> setOf(EntityType.MENTION, EntityType.TEXTMENTION).contains(e.type) }

        fun runChatGPT(ctx: MessageContext) =
            ce.execute(ChatGPT(ctx), isLongtimeCommand = true)

        if (update.hasMessage() && update.message.isReply) {
            val message = update.message

            if (message.hasText()) {
                val text = message.text
                val replyToMessage = message.replyToMessage
                val ctx = MessageContext(this, update, "")

                if (!replyToMessage.hasPhoto()) {
                    if (replyToMessage.from.id == me.id && !text.startsWith("/")) {
                        if (!message.hasEntities())
                            runChatGPT(ctx)
                        else
                            if (!hasMentions(message))
                                runChatGPT(ctx)
                    }
                } else
                    if (text == "@")
                        ce.execute(DalleVariations(ctx), isLongtimeCommand = true)
            }
        }

        commands.handleUpdate(this, update)

        return null
    }
}