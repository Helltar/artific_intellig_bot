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
import com.helltar.artific_intellig_bot.BotConfig.Companion.BOT_TOKEN
import com.helltar.artific_intellig_bot.BotConfig.Companion.BOT_USERNAME
import com.helltar.artific_intellig_bot.BotConfig.Companion.DIR_DB
import com.helltar.artific_intellig_bot.BotConfig.Companion.DIR_STABLE_DIFFUSION
import com.helltar.artific_intellig_bot.BotConfig.Companion.DIR_TEXT_TO_SPEECH
import com.helltar.artific_intellig_bot.commands.*
import com.helltar.artific_intellig_bot.commands.Commands.commandChatAsText
import com.helltar.artific_intellig_bot.commands.Commands.commandChatAsVoice
import com.helltar.artific_intellig_bot.commands.Commands.commandDisable
import com.helltar.artific_intellig_bot.commands.Commands.commandEnable
import com.helltar.artific_intellig_bot.commands.Commands.commandAbout
import com.helltar.artific_intellig_bot.commands.Commands.commandChat
import com.helltar.artific_intellig_bot.commands.Commands.commandDalle
import com.helltar.artific_intellig_bot.commands.Commands.commandStableDiffusion
import com.helltar.artific_intellig_bot.commands.admin.ChatAsTextCommand
import com.helltar.artific_intellig_bot.commands.admin.ChatAsVoiceCommand
import com.helltar.artific_intellig_bot.commands.admin.DisableCommand
import com.helltar.artific_intellig_bot.commands.admin.EnableCommand
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

    log.info("start ...")

    bot {
        token = BOT_TOKEN
        logLevel = LogLevel.Error

        dispatch {
            command(commandChat) { runCommand(ChatGPTCommand(bot, update.message!!, args), commandChat) }
            command(commandDalle) { runCommand(DallE2Command(bot, update.message!!, args), commandDalle) }
            command(commandStableDiffusion) { runCommand(StableDiffusionCommand(bot, update.message!!, args), commandStableDiffusion) }
            command(commandAbout) { runCommand(AboutCommand(bot, update.message!!), commandAbout) }

            command(commandEnable) { runCommand(EnableCommand(bot, update.message!!, args), commandEnable) }
            command(commandDisable) { runCommand(DisableCommand(bot, update.message!!, args), commandDisable) }
            command(commandChatAsText) { runCommand(ChatAsTextCommand(bot, update.message!!), commandChatAsText) }
            command(commandChatAsVoice) { runCommand(ChatAsVoiceCommand(bot, update.message!!), commandChatAsVoice) }

            message(Filter.Reply) {
                val replyToMessage = update.message!!.replyToMessage!!
                val text = update.message!!.text ?: "/"

                if (text.startsWith("/")) return@message
                if (replyToMessage.photo != null) return@message

                if (replyToMessage.from!!.username == BOT_USERNAME)
                    runCommand(ChatGPTCommand(bot, update.message!!, listOf("reply")), commandChat)
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

    if (botCommand.isChatInWhiteList(commandName))
        if (botCommand.isCommandEnable(commandName))
            addRequest("$classSimpleName@$userId", botCommand.bot, botCommand.message) {
                botCommand.run()
            }
        else
            botCommand.sendMessage(Strings.command_disabled)
    else
        botCommand.sendMessage(Strings.command_not_supported_in_chat)
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
