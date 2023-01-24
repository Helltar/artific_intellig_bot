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
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsText
import com.helltar.artific_intellig_bot.commands.Commands.cmdChatAsVoice
import com.helltar.artific_intellig_bot.commands.Commands.cmdDisable
import com.helltar.artific_intellig_bot.commands.Commands.cmdEnable
import com.helltar.artific_intellig_bot.commands.Commands.cmdAbout
import com.helltar.artific_intellig_bot.commands.Commands.cmdBanList
import com.helltar.artific_intellig_bot.commands.Commands.cmdBanUser
import com.helltar.artific_intellig_bot.commands.Commands.cmdChat
import com.helltar.artific_intellig_bot.commands.Commands.cmdDalle
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

            command(cmdEnable) { runCommand(EnableCommand(bot, update.message!!, args), cmdEnable) }
            command(cmdDisable) { runCommand(DisableCommand(bot, update.message!!, args), cmdDisable) }
            command(cmdChatAsText) { runCommand(ChatAsTextCommand(bot, update.message!!), cmdChatAsText) }
            command(cmdChatAsVoice) { runCommand(ChatAsVoiceCommand(bot, update.message!!), cmdChatAsVoice) }
            command(cmdBanUser) { runCommand(BanUserCommand(bot, update.message!!, args), cmdBanUser) }
            command(cmdUnbanUser) { runCommand(UnbanUserCommand(bot, update.message!!), cmdUnbanUser) }
            command(cmdBanList) { runCommand(BanListCommand(bot, update.message!!), cmdBanList) }

            command(cmdAbout) { runCommand(AboutCommand(bot, update.message!!), cmdAbout) }

            message(Filter.Reply) {
                val replyToMessage = update.message!!.replyToMessage!!
                val text = update.message!!.text ?: "/"

                if (text.startsWith("/")) return@message
                if (replyToMessage.photo != null) return@message

                if (replyToMessage.from!!.username == BOT_USERNAME)
                    runCommand(ChatGPTCommand(bot, update.message!!, listOf("reply")), cmdChat)
            }

            telegramError { log.error(error.getErrorMessage()) }
        }
    }
        .startPolling()
}

private fun runCommand(botCommand: BotCommand, commandName: String) {
    val user = botCommand.message.from ?: return
    val userId = user.id
    val chat = botCommand.message.chat
    val classSimpleName = botCommand.javaClass.simpleName

    log.info("$classSimpleName: ${chat.id} $userId ${user.username} ${user.firstName} ${chat.title} : ${botCommand.args}")

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
                sendMessage("❌ BAN")
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
