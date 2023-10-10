package com.helltar.aibot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.SimpleCommand
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.ArtificIntelligBot.Companion.addRequest
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands.CMD_ABOUT
import com.helltar.aibot.commands.Commands.CMD_ADDADMIN
import com.helltar.aibot.commands.Commands.CMD_ADD_CHAT
import com.helltar.aibot.commands.Commands.CMD_ADMIN_LIST
import com.helltar.aibot.commands.Commands.CMD_BAN_LIST
import com.helltar.aibot.commands.Commands.CMD_BAN_USER
import com.helltar.aibot.commands.Commands.CMD_CHAT
import com.helltar.aibot.commands.Commands.CMD_CHATCTX
import com.helltar.aibot.commands.Commands.CMD_CHATS_WHITE_LIST
import com.helltar.aibot.commands.Commands.CMD_CHAT_AS_TEXT
import com.helltar.aibot.commands.Commands.CMD_CHAT_AS_VOICE
import com.helltar.aibot.commands.Commands.CMD_CHAT_CTX_REMOVE
import com.helltar.aibot.commands.Commands.CMD_DALLE
import com.helltar.aibot.commands.Commands.CMD_DALLE_VARIATIONS
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
import com.helltar.aibot.commands.admin.command.ChatAsText
import com.helltar.aibot.commands.admin.command.ChatAsVoice
import com.helltar.aibot.commands.admin.slowmode.SlowMode
import com.helltar.aibot.commands.admin.slowmode.SlowModeOff
import com.helltar.aibot.commands.user.About
import com.helltar.aibot.commands.user.MyId
import com.helltar.aibot.commands.user.Start
import com.helltar.aibot.commands.user.Uptime
import com.helltar.aibot.commands.user.chat.ChatCtx
import com.helltar.aibot.commands.user.chat.ChatCtxRemove
import com.helltar.aibot.commands.user.chat.ChatGPT
import com.helltar.aibot.commands.user.images.DallE2
import com.helltar.aibot.commands.user.images.DalleVariations
import com.helltar.aibot.commands.user.images.StableDiffusion
import com.helltar.aibot.commands.user.lists.BanList
import com.helltar.aibot.commands.user.lists.SlowModeList
import com.helltar.aibot.dao.DatabaseFactory
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class ArtificIntelligBotHandler(private val botConfig: BotConfig.JsonData) : BotHandler(botConfig.token) {

    private val authority = SimpleAuthority(botConfig.creatorId)
    private val commands = CommandRegistry(botConfig.username, authority)

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        commands.run {
            register(simpleCommand(CMD_START) { runCommand(Start(it), CMD_START, checkRights = false) })
            register(simpleCommand(CMD_MYID) { runCommand(MyId(it), CMD_MYID, checkRights = false) })
            register(simpleCommand(CMD_ABOUT) { runCommand(About(it), CMD_ABOUT, checkRights = false) })

            register(simpleCommand(CMD_CHAT) { runCommand(ChatGPT(it), CMD_CHAT) })
            register(simpleCommand(CMD_CHATCTX) { runCommand(ChatCtx(it), CMD_CHATCTX) })
            register(simpleCommand(CMD_CHAT_CTX_REMOVE) { runCommand(ChatCtxRemove(it), CMD_CHAT_CTX_REMOVE) })

            register(simpleCommand(CMD_DALLE) { runCommand(DallE2(it), CMD_DALLE) })
            register(simpleCommand(CMD_SDIFF) { runCommand(StableDiffusion(it), CMD_SDIFF) })
            register(simpleCommand(CMD_BAN_LIST) { runCommand(BanList(it), CMD_BAN_LIST) })
            register(simpleCommand(CMD_UPTIME) { runCommand(Uptime(it), CMD_UPTIME) })
            register(simpleCommand(CMD_SLOW_MODE_LIST) { runCommand(SlowModeList(it), CMD_SLOW_MODE_LIST) })

            register(simpleCommand(CMD_ENABLE) { runCommand(ChangeState(it), CMD_ENABLE, true) })
            register(simpleCommand(CMD_DISABLE) { runCommand(ChangeState(it, true), CMD_DISABLE, true) })
            register(simpleCommand(CMD_CHAT_AS_TEXT) { runCommand(ChatAsText(it), CMD_CHAT_AS_TEXT, true) })
            register(simpleCommand(CMD_CHAT_AS_VOICE) { runCommand(ChatAsVoice(it), CMD_CHAT_AS_VOICE, true) })
            register(simpleCommand(CMD_BAN_USER) { runCommand(BanUser(it), CMD_BAN_USER, true) })
            register(simpleCommand(CMD_UNBAN_USER) { runCommand(UnbanUser(it), CMD_UNBAN_USER, true) })
            register(simpleCommand(CMD_SLOW_MODE) { runCommand(SlowMode(it), CMD_SLOW_MODE, true) })
            register(simpleCommand(CMD_SLOW_MODE_OFF) { runCommand(SlowModeOff(it), CMD_SLOW_MODE_OFF, true) })

            register(simpleCommand(CMD_ADDADMIN) { runCommand(AddAdmin(it), CMD_ADDADMIN, isCreatorCommand = true) })
            register(simpleCommand(CMD_RM_ADMIN) { runCommand(RemoveAdmin(it), CMD_RM_ADMIN, true) })
            register(simpleCommand(CMD_ADMIN_LIST) { runCommand(AdminList(it), CMD_ADMIN_LIST, true) })

            register(simpleCommand(CMD_CHATS_WHITE_LIST) { runCommand(ChatsWhiteList(it), CMD_CHATS_WHITE_LIST, true) })
            register(simpleCommand(CMD_ADD_CHAT) { runCommand(AddChat(it), CMD_ADD_CHAT, isCreatorCommand = true) })
            register(simpleCommand(CMD_RM_CHAT) { runCommand(RemoveChat(it), CMD_RM_CHAT, true) })
        }
    }

    private fun simpleCommand(command: String, c: Consumer<MessageContext>) =
        SimpleCommand("/$command", c)

    override fun getBotUsername() =
        botConfig.username

    override fun onUpdate(update: Update): BotApiMethod<*>? {

        fun hasMentions(message: Message) =
            message.entities.stream().anyMatch { e -> setOf(EntityType.MENTION, EntityType.TEXTMENTION).contains(e.type) }

        fun runChatGPT(ctx: MessageContext) =
            runCommand(ChatGPT(ctx), CMD_CHAT)

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
                        runCommand(DalleVariations(ctx), CMD_DALLE_VARIATIONS)
            }
        }

        commands.handleUpdate(this, update)

        return null
    }

    private fun runCommand(
        botCommand: BotCommand,
        command: String,
        isAdminCommand: Boolean = false,
        isCreatorCommand: Boolean = false,
        checkRights: Boolean = true
    ) {

        val user = botCommand.ctx.user()
        val userId = user.id
        val chat = botCommand.ctx.message().chat

        log.info("$command: ${chat.id} $userId ${user.userName} ${user.firstName} ${chat.title} : ${botCommand.ctx.message().text}")

        botCommand.run {
            if (!checkRights)
                return@run

            if (isCreatorCommand && !isCreator())
                return

            if (isAdminCommand && isNotAdmin())
                return

            if (isCreator() or isAdmin())
                return@run

            if (isUserBanned(userId)) {
                val reason = DatabaseFactory.banList.getReason(userId) ?: "\uD83E\uDD37\u200D♂️" // 🤷‍♂️
                replyToMessage(String.format(Strings.BAN_AND_REASON, reason))
                return
            }

            if (!isChatInWhiteList()) {
                replyToMessage(Strings.COMMAND_NOT_SUPPORTED_IN_CHAT)
                return
            }

            if (isCommandDisabled(command)) {
                replyToMessage(Strings.COMMAND_TEMPORARY_DISABLED)
                return
            }

            var userRequests = DatabaseFactory.slowMode.getRequestsSize(userId)

            if (userRequests == -1)
                return@run

            val lastRequest = DatabaseFactory.slowMode.getLastRequestTimestamp(userId)
            val limit = DatabaseFactory.slowMode.getLimitSize(userId)

            if (userRequests >= limit) {
                if ((lastRequest + TimeUnit.HOURS.toMillis(1)) > System.currentTimeMillis()) {
                    val timestamp = System.currentTimeMillis()
                    val remainingSeconds = TimeUnit.MILLISECONDS.toSeconds((lastRequest + TimeUnit.HOURS.toMillis(1)) - timestamp)
                    replyToMessage(String.format(Strings.SLOW_MODE_PLEASE_WAIT, remainingSeconds))
                    return
                } else
                    userRequests = 0
            }

            DatabaseFactory.slowMode.update(user, limit, userRequests + 1)
        }

        addRequest("${botCommand.javaClass.simpleName}@$userId", botCommand.ctx) { botCommand.run() }
    }
}