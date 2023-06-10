package com.helltar.artific_intellig_bot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.commands.CommandRegistry
import com.annimon.tgbotsmodule.commands.SimpleCommand
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.artific_intellig_bot.ArtificIntelligBot.Companion.addRequest
import com.helltar.artific_intellig_bot.commands.*
import com.helltar.artific_intellig_bot.commands.Commands.cmdAbout
import com.helltar.artific_intellig_bot.commands.Commands.cmdAddAdmin
import com.helltar.artific_intellig_bot.commands.Commands.cmdAddChat
import com.helltar.artific_intellig_bot.commands.Commands.cmdAdminList
import com.helltar.artific_intellig_bot.commands.Commands.cmdBanList
import com.helltar.artific_intellig_bot.commands.Commands.cmdBanUser
import com.helltar.artific_intellig_bot.commands.Commands.cmdChat
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsText
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsVoice
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatWhiteList
import com.helltar.artific_intellig_bot.commands.Commands.cmdDalle
import com.helltar.artific_intellig_bot.commands.Commands.cmdDalleVariations
import com.helltar.artific_intellig_bot.commands.Commands.cmdDisable
import com.helltar.artific_intellig_bot.commands.Commands.cmdEnable
import com.helltar.artific_intellig_bot.commands.Commands.cmdRmAdmin
import com.helltar.artific_intellig_bot.commands.Commands.cmdRmChat
import com.helltar.artific_intellig_bot.commands.Commands.cmdSDiff
import com.helltar.artific_intellig_bot.commands.Commands.cmdStart
import com.helltar.artific_intellig_bot.commands.Commands.cmdUnbanUser
import com.helltar.artific_intellig_bot.commands.Commands.cmdUptime
import com.helltar.artific_intellig_bot.commands.admin.*
import com.helltar.artific_intellig_bot.db.Database
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class ArtificIntelligBotHandler(private val botConfig: BotMainConfig) : BotHandler(botConfig.token) {

    private val authority = SimpleAuthority(botConfig.creatorId)
    private val commands = CommandRegistry(botConfig.username, authority)

    init {
        commands.run {
            register(SimpleCommand(cmdStart) { runCommand(StartCommand(it), cmdStart) })

            register(SimpleCommand(cmdChat) { runCommand(ChatGPTCommand(it, botConfig.chatGptSystemMessage), cmdChat) })
            register(SimpleCommand(cmdDalle) { runCommand(DallE2Command(it), cmdDalle) })
            register(SimpleCommand(cmdSDiff) { runCommand(StableDiffusionCommand(it), cmdSDiff) })
            register(SimpleCommand(cmdBanList) { runCommand(BanListCommand(it), cmdBanList) })
            register(SimpleCommand(cmdAbout) { runCommand(AboutCommand(it), cmdAbout) })
            register(SimpleCommand(cmdUptime) { runCommand(UptimeCommand(it), cmdUptime) })

            register(SimpleCommand(cmdEnable) { runCommand(EnableCommand(it), cmdEnable, true) })
            register(SimpleCommand(cmdDisable) { runCommand(DisableCommand(it), cmdDisable, true) })
            register(SimpleCommand(cmdChatAsText) { runCommand(ChatAsTextCommand(it), cmdChatAsText, true) })
            register(SimpleCommand(cmdChatAsVoice) { runCommand(ChatAsVoiceCommand(it), cmdChatAsVoice, true) })
            register(SimpleCommand(cmdBanUser) { runCommand(BanUserCommand(it), cmdBanUser, true) })
            register(SimpleCommand(cmdUnbanUser) { runCommand(UnbanUserCommand(it), cmdUnbanUser, true) })

            register(SimpleCommand(cmdAddAdmin) { runCommand(AddAdminCommand(it), cmdAddAdmin, isCreatorCommand = true) })
            register(SimpleCommand(cmdRmAdmin) { runCommand(RemoveAdminCommand(it), cmdRmAdmin, true) })
            register(SimpleCommand(cmdAdminList) { runCommand(AdminListCommand(it), cmdAdminList, true) })

            register(SimpleCommand(cmdChatWhiteList) { runCommand(ChatWhiteListCommand(it), cmdChatWhiteList, true) })
            register(SimpleCommand(cmdAddChat) { runCommand(AddChatCommand(it), cmdAddChat, isCreatorCommand = true) })
            register(SimpleCommand(cmdRmChat) { runCommand(RemoveChatCommand(it), cmdRmChat, true) })
        }
    }

    override fun getBotUsername() =
        botConfig.username

    override fun onUpdate(update: Update): BotApiMethod<*>? {

        fun hasMentions(message: Message) =
            message.entities.stream().anyMatch { e -> setOf(EntityType.MENTION, EntityType.TEXTMENTION).contains(e.type) }

        fun runChatGPT(ctx: MessageContext) =
            runCommand(ChatGPTCommand(ctx, chatSystemMessage = botConfig.chatGptSystemMessage), cmdChat)

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

    private fun runCommand(botCommand: BotCommand, commandName: String, isAdminCommand: Boolean = false, isCreatorCommand: Boolean = false) {
        val user = botCommand.ctx.user()
        val userId = user.id
        val chat = botCommand.ctx.message().chat

        LoggerFactory.getLogger(javaClass)
            .info("$commandName: ${chat.id} $userId ${user.userName} ${user.firstName} ${chat.title} : ${botCommand.ctx.argumentsAsString()}")

        botCommand.run {
            if (commandName == cmdStart)
                return@run

            if (isCreatorCommand && !isCreator())
                return

            if (isAdminCommand && isNotAdmin())
                return

            if (isCreator() or isAdmin())
                return@run

            if (isUserBanned(userId)) {
                val reason = Database.banList.getReason(userId).ifEmpty { "\uD83E\uDD37\u200D♂️" } // 🤷‍♂️
                replyToMessage(String.format(Strings.ban_and_reason, reason))
                return
            }

            if (!isChatInWhiteList()) {
                replyToMessage(Strings.command_not_supported_in_chat)
                return
            }

            if (isCommandDisabled(commandName)) {
                replyToMessage(Strings.command_temporary_disabled)
                return
            }
        }

        addRequest("${botCommand.javaClass.simpleName}@$userId", botCommand.ctx) { botCommand.run() }
    }
}