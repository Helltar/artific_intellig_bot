package com.helltar.artific_intellig_bot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.SimpleCommand
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.ArtificIntelligBot.Companion.addRequest
import com.helltar.artific_intellig_bot.Commands.cmdAbout
import com.helltar.artific_intellig_bot.Commands.cmdAddAdmin
import com.helltar.artific_intellig_bot.Commands.cmdAddChat
import com.helltar.artific_intellig_bot.Commands.cmdAdminList
import com.helltar.artific_intellig_bot.Commands.cmdBanList
import com.helltar.artific_intellig_bot.Commands.cmdBanUser
import com.helltar.artific_intellig_bot.Commands.cmdChat
import com.helltar.artific_intellig_bot.Commands.cmdChatAsText
import com.helltar.artific_intellig_bot.Commands.cmdChatAsVoice
import com.helltar.artific_intellig_bot.Commands.cmdChatCtx
import com.helltar.artific_intellig_bot.Commands.cmdChatCtxRemove
import com.helltar.artific_intellig_bot.Commands.cmdChatWhiteList
import com.helltar.artific_intellig_bot.Commands.cmdDalle
import com.helltar.artific_intellig_bot.Commands.cmdDalleVariations
import com.helltar.artific_intellig_bot.Commands.cmdDisable
import com.helltar.artific_intellig_bot.Commands.cmdEnable
import com.helltar.artific_intellig_bot.Commands.cmdMyId
import com.helltar.artific_intellig_bot.Commands.cmdRmAdmin
import com.helltar.artific_intellig_bot.Commands.cmdRmChat
import com.helltar.artific_intellig_bot.Commands.cmdSDiff
import com.helltar.artific_intellig_bot.Commands.cmdSlowMode
import com.helltar.artific_intellig_bot.Commands.cmdSlowModeList
import com.helltar.artific_intellig_bot.Commands.cmdSlowModeOff
import com.helltar.artific_intellig_bot.Commands.cmdStart
import com.helltar.artific_intellig_bot.Commands.cmdUnbanUser
import com.helltar.artific_intellig_bot.Commands.cmdUptime
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.admin.*
import com.helltar.artific_intellig_bot.commands.user.*
import com.helltar.artific_intellig_bot.commands.user.chat.ChatCtxCommand
import com.helltar.artific_intellig_bot.commands.user.chat.ChatCtxRemoveCommand
import com.helltar.artific_intellig_bot.commands.user.chat.ChatGPTCommand
import com.helltar.artific_intellig_bot.dao.DatabaseFactory
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class ArtificIntelligBotHandler(private val botConfig: BotMainConfig) : BotHandler(botConfig.token) {

    private val authority = SimpleAuthority(botConfig.creatorId)
    private val commands = CommandRegistry(botConfig.username, authority)

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        commands.run {
            register(simpleCommand(cmdStart) { runCommand(StartCommand(it), cmdStart, checkRights = false) })
            register(simpleCommand(cmdMyId) { runCommand(MyIdCommand(it), cmdMyId, checkRights = false) })
            register(simpleCommand(cmdAbout) { runCommand(AboutCommand(it), cmdAbout, checkRights = false) })

            register(simpleCommand(cmdChat) { runCommand(ChatGPTCommand(it), cmdChat) })
            register(simpleCommand(cmdChatCtx) { runCommand(ChatCtxCommand(it), cmdChatCtx) })
            register(simpleCommand(cmdChatCtxRemove) { runCommand(ChatCtxRemoveCommand(it), cmdChatCtxRemove) })

            register(simpleCommand(cmdDalle) { runCommand(DallE2Command(it), cmdDalle) })
            register(simpleCommand(cmdSDiff) { runCommand(StableDiffusionCommand(it), cmdSDiff) })
            register(simpleCommand(cmdBanList) { runCommand(BanListCommand(it), cmdBanList) })
            register(simpleCommand(cmdUptime) { runCommand(UptimeCommand(it), cmdUptime) })
            register(simpleCommand(cmdSlowModeList) { runCommand(SlowModeListCommand(it), cmdSlowModeList) })

            register(simpleCommand(cmdEnable) { runCommand(ChangeStateCommand(it), cmdEnable, true) })
            register(simpleCommand(cmdDisable) { runCommand(ChangeStateCommand(it, true), cmdDisable, true) })
            register(simpleCommand(cmdChatAsText) { runCommand(ChatAsTextCommand(it), cmdChatAsText, true) })
            register(simpleCommand(cmdChatAsVoice) { runCommand(ChatAsVoiceCommand(it), cmdChatAsVoice, true) })
            register(simpleCommand(cmdBanUser) { runCommand(BanUserCommand(it), cmdBanUser, true) })
            register(simpleCommand(cmdUnbanUser) { runCommand(UnbanUserCommand(it), cmdUnbanUser, true) })
            register(simpleCommand(cmdSlowMode) { runCommand(SlowModeCommand(it), cmdSlowMode, true) })
            register(simpleCommand(cmdSlowModeOff) { runCommand(SlowModeOffCommand(it), cmdSlowModeOff, true) })

            register(simpleCommand(cmdAddAdmin) { runCommand(AddAdminCommand(it), cmdAddAdmin, isCreatorCommand = true) })
            register(simpleCommand(cmdRmAdmin) { runCommand(RemoveAdminCommand(it), cmdRmAdmin, true) })
            register(simpleCommand(cmdAdminList) { runCommand(AdminListCommand(it), cmdAdminList, true) })

            register(simpleCommand(cmdChatWhiteList) { runCommand(ChatWhiteListCommand(it), cmdChatWhiteList, true) })
            register(simpleCommand(cmdAddChat) { runCommand(AddChatCommand(it), cmdAddChat, isCreatorCommand = true) })
            register(simpleCommand(cmdRmChat) { runCommand(RemoveChatCommand(it), cmdRmChat, true) })
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
            runCommand(ChatGPTCommand(ctx), cmdChat)

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
                        runCommand(DalleVariationsCommand(ctx), cmdDalleVariations)
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

        log.info("$command: ${chat.id} $userId ${user.userName} ${user.firstName} ${chat.title} : ${botCommand.ctx.argumentsAsString()}")

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

            val userRequests = DatabaseFactory.slowMode.getRequestsSize(userId)

            if (userRequests == -1)
                return@run

            val lastRequest = DatabaseFactory.slowMode.getLastRequestTimestamp(userId)
            val limit = DatabaseFactory.slowMode.getLimitSize(userId)

            if (userRequests >= limit) {
                if ((lastRequest + TimeUnit.HOURS.toMillis(1)) > System.currentTimeMillis()) {
                    replyToMessage(String.format(Strings.slow_mode_please_wait, TimeUnit.NANOSECONDS.toSeconds(lastRequest)))
                    return
                } else {
                    DatabaseFactory.slowMode.update(user, limit, 0)
                    return@run
                }
            }

            DatabaseFactory.slowMode.update(user, limit, userRequests + 1)
        }

        addRequest("${botCommand.javaClass.simpleName}@$userId", botCommand.ctx) { botCommand.run() }
    }
}