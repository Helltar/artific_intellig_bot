package com.helltar.aibot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.SimpleCommand
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.ArtificIntelligBot.Companion.addRequest
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands.cmdAbout
import com.helltar.aibot.commands.Commands.cmdAddAdmin
import com.helltar.aibot.commands.Commands.cmdAddChat
import com.helltar.aibot.commands.Commands.cmdAdminList
import com.helltar.aibot.commands.Commands.cmdBanList
import com.helltar.aibot.commands.Commands.cmdBanUser
import com.helltar.aibot.commands.Commands.cmdChat
import com.helltar.aibot.commands.Commands.cmdChatAsText
import com.helltar.aibot.commands.Commands.cmdChatAsVoice
import com.helltar.aibot.commands.Commands.cmdChatCtx
import com.helltar.aibot.commands.Commands.cmdChatCtxRemove
import com.helltar.aibot.commands.Commands.cmdChatWhiteList
import com.helltar.aibot.commands.Commands.cmdDalle
import com.helltar.aibot.commands.Commands.cmdDalleVariations
import com.helltar.aibot.commands.Commands.cmdDisable
import com.helltar.aibot.commands.Commands.cmdEnable
import com.helltar.aibot.commands.Commands.cmdMyId
import com.helltar.aibot.commands.Commands.cmdRmAdmin
import com.helltar.aibot.commands.Commands.cmdRmChat
import com.helltar.aibot.commands.Commands.cmdSDiff
import com.helltar.aibot.commands.Commands.cmdSlowMode
import com.helltar.aibot.commands.Commands.cmdSlowModeList
import com.helltar.aibot.commands.Commands.cmdSlowModeOff
import com.helltar.aibot.commands.Commands.cmdStart
import com.helltar.aibot.commands.Commands.cmdUnbanUser
import com.helltar.aibot.commands.Commands.cmdUptime
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
            register(simpleCommand(cmdStart) { runCommand(Start(it), cmdStart, checkRights = false) })
            register(simpleCommand(cmdMyId) { runCommand(MyId(it), cmdMyId, checkRights = false) })
            register(simpleCommand(cmdAbout) { runCommand(About(it), cmdAbout, checkRights = false) })

            register(simpleCommand(cmdChat) { runCommand(ChatGPT(it), cmdChat) })
            register(simpleCommand(cmdChatCtx) { runCommand(ChatCtx(it), cmdChatCtx) })
            register(simpleCommand(cmdChatCtxRemove) { runCommand(ChatCtxRemove(it), cmdChatCtxRemove) })

            register(simpleCommand(cmdDalle) { runCommand(DallE2(it), cmdDalle) })
            register(simpleCommand(cmdSDiff) { runCommand(StableDiffusion(it), cmdSDiff) })
            register(simpleCommand(cmdBanList) { runCommand(BanList(it), cmdBanList) })
            register(simpleCommand(cmdUptime) { runCommand(Uptime(it), cmdUptime) })
            register(simpleCommand(cmdSlowModeList) { runCommand(SlowModeList(it), cmdSlowModeList) })

            register(simpleCommand(cmdEnable) { runCommand(ChangeState(it), cmdEnable, true) })
            register(simpleCommand(cmdDisable) { runCommand(ChangeState(it, true), cmdDisable, true) })
            register(simpleCommand(cmdChatAsText) { runCommand(ChatAsText(it), cmdChatAsText, true) })
            register(simpleCommand(cmdChatAsVoice) { runCommand(ChatAsVoice(it), cmdChatAsVoice, true) })
            register(simpleCommand(cmdBanUser) { runCommand(BanUser(it), cmdBanUser, true) })
            register(simpleCommand(cmdUnbanUser) { runCommand(UnbanUser(it), cmdUnbanUser, true) })
            register(simpleCommand(cmdSlowMode) { runCommand(SlowMode(it), cmdSlowMode, true) })
            register(simpleCommand(cmdSlowModeOff) { runCommand(SlowModeOff(it), cmdSlowModeOff, true) })

            register(simpleCommand(cmdAddAdmin) { runCommand(AddAdmin(it), cmdAddAdmin, isCreatorCommand = true) })
            register(simpleCommand(cmdRmAdmin) { runCommand(RemoveAdmin(it), cmdRmAdmin, true) })
            register(simpleCommand(cmdAdminList) { runCommand(AdminList(it), cmdAdminList, true) })

            register(simpleCommand(cmdChatWhiteList) { runCommand(ChatsWhiteList(it), cmdChatWhiteList, true) })
            register(simpleCommand(cmdAddChat) { runCommand(AddChat(it), cmdAddChat, isCreatorCommand = true) })
            register(simpleCommand(cmdRmChat) { runCommand(RemoveChat(it), cmdRmChat, true) })
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
            runCommand(ChatGPT(ctx), cmdChat)

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
                        runCommand(DalleVariations(ctx), cmdDalleVariations)
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
                replyToMessage(String.format(Strings.ban_and_reason, reason))
                return
            }

            if (!isChatInWhiteList()) {
                replyToMessage(Strings.command_not_supported_in_chat)
                return
            }

            if (isCommandDisabled(command)) {
                replyToMessage(Strings.command_temporary_disabled)
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
                    replyToMessage(String.format(Strings.slow_mode_please_wait, remainingSeconds))
                    return
                } else
                    userRequests = 0
            }

            DatabaseFactory.slowMode.update(user, limit, userRequests + 1)
        }

        addRequest("${botCommand.javaClass.simpleName}@$userId", botCommand.ctx) { botCommand.run() }
    }
}