package com.helltar.artific_intellig_bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import com.helltar.artific_intellig_bot.commands.*
import com.helltar.artific_intellig_bot.commands.Commands.cmdAbout
import com.helltar.artific_intellig_bot.commands.Commands.cmdBanList
import com.helltar.artific_intellig_bot.commands.Commands.cmdBanUser
import com.helltar.artific_intellig_bot.commands.Commands.cmdChat
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsText
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsVoice
import com.helltar.artific_intellig_bot.commands.Commands.cmdDalle
import com.helltar.artific_intellig_bot.commands.Commands.cmdDisable
import com.helltar.artific_intellig_bot.commands.Commands.cmdEnable
import com.helltar.artific_intellig_bot.commands.Commands.cmdStableDiffusion
import com.helltar.artific_intellig_bot.commands.Commands.cmdUnbanUser
import com.helltar.artific_intellig_bot.commands.admin.*
import com.helltar.artific_intellig_bot.db.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

private val log = LoggerFactory.getLogger("Main")
private val requestList = hashMapOf<String, Job>()

fun main() {

    mkDirs()
    Database.init()

    log.info("start ...")

    bot {
        token = BOT_TOKEN
        logLevel = LogLevel.Error

        dispatch {
            command(cmdChat) { runCommand(ChatGPTCommand(bot, update.message!!, args), cmdChat) }
            command(cmdDalle) { runCommand(DallE2Command(bot, update.message!!, args), cmdDalle) }
            command(cmdStableDiffusion) { runCommand(StableDiffusionCommand(bot, update.message!!, args), cmdStableDiffusion) }
            command(cmdBanList) { runCommand(BanListCommand(bot, update.message!!), cmdBanList) }
            command(cmdAbout) { runCommand(AboutCommand(bot, update.message!!), cmdAbout) }

            command(cmdEnable) { runCommand(EnableCommand(bot, update.message!!, args), cmdEnable, true) }
            command(cmdDisable) { runCommand(DisableCommand(bot, update.message!!, args), cmdDisable, true) }
            command(cmdChatAsText) { runCommand(ChatAsTextCommand(bot, update.message!!), cmdChatAsText, true) }
            command(cmdChatAsVoice) { runCommand(ChatAsVoiceCommand(bot, update.message!!), cmdChatAsVoice, true) }
            command(cmdBanUser) { runCommand(BanUserCommand(bot, update.message!!, args), cmdBanUser, true) }
            command(cmdUnbanUser) { runCommand(UnbanUserCommand(bot, update.message!!, args), cmdUnbanUser, true) }

            message(Filter.Reply) {
                update.message?.let {
                    it.replyToMessage?.photo
                        ?: it.text?.let { text ->
                            if (it.replyToMessage?.from?.username == bot.getMe().get().username)
                                if (!text.startsWith("/"))
                                    runCommand(ChatGPTCommand(bot, it, listOf("reply")), cmdChat)
                        }
                }
            }

            telegramError { log.error(error.getErrorMessage()) }
        }
    }
        .startPolling()
}

private fun runCommand(botCommand: BotCommand, commandName: String, isAdminCommand: Boolean = false) {
    val user = botCommand.message.from ?: return
    val userId = user.id
    val chat = botCommand.message.chat
    val classSimpleName = botCommand.javaClass.simpleName

    log.info("$classSimpleName: ${chat.id} $userId ${user.username} ${user.firstName} ${chat.title} : ${botCommand.args}")

    if (isAdminCommand)
        if (botCommand.isNotAdmin())
            return

    botCommand.run {
        if (isNotAdmin()) {
            if (isChatNotInWhiteList()) {
                sendMessage(Strings.command_not_supported_in_chat)
                return
            }

            if (isCommandDisabled(commandName)) {
                sendMessage(Strings.command_disabled)
                return
            }

            if (isUserBanned(userId)) {
                val reason = Database.banListTable.getReason(userId).ifEmpty { "null" }
                sendMessage("❌ Ban, reason: <b>$reason</b>")
                return
            }
        }
    }

    addRequest("$classSimpleName@$userId", botCommand.bot, botCommand.message) {
        botCommand.run()
    }
}

private fun addRequest(requestKey: String, bot: Bot, message: Message, func: () -> Unit) {
    if (requestList.containsKey(requestKey))
        if (requestList[requestKey]?.isCompleted == false) {
            bot.sendMessage(
                ChatId.fromId(message.chat.id),
                Strings.many_request,
                replyToMessageId = message.messageId, allowSendingWithoutReply = true
            )

            return
        }

    requestList[requestKey] = CoroutineScope(Dispatchers.Default).launch { func() }
}

private fun mkDirs() {
    File(DIR_DB).mkdir()
    File(DIR_STABLE_DIFFUSION).mkdir()
    File(DIR_TEXT_TO_SPEECH).mkdir()
}
