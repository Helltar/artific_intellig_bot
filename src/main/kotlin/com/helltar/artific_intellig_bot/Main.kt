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
import com.helltar.artific_intellig_bot.BotConfig.BOT_TOKEN
import com.helltar.artific_intellig_bot.BotConfig.BOT_USERNAME
import com.helltar.artific_intellig_bot.BotConfig.DIR_STABLE_DIFFUSION
import com.helltar.artific_intellig_bot.commands.BotCommand
import com.helltar.artific_intellig_bot.commands.ChatGPTCommand
import com.helltar.artific_intellig_bot.commands.DallE2Command
import com.helltar.artific_intellig_bot.commands.StableDiffusionCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

private val log = LoggerFactory.getLogger("Main")
private val requestList = hashMapOf<String, Job>()

fun main() {

    File(DIR_STABLE_DIFFUSION).mkdir()

    log.info("start ...")

    bot {
        token = BOT_TOKEN
        logLevel = LogLevel.Error

        dispatch {
            command("chat") { runCommand(ChatGPTCommand(bot, update.message!!, args)) }
            command("dalle") { runCommand(DallE2Command(bot, update.message!!, args)) }
            command("sdif") { runCommand(StableDiffusionCommand(bot, update.message!!, args)) }

            message(Filter.Reply) {
                if (update.message!!.replyToMessage!!.from!!.username == BOT_USERNAME)
                    runCommand(ChatGPTCommand(bot, update.message!!, listOf("reply")))
            }

            telegramError { log.error(error.getErrorMessage()) }
        }
    }
        .startPolling()
}

private fun runCommand(botCommand: BotCommand) {
    val user = botCommand.message.from ?: return
    val userId = user.id
    val chat = botCommand.message.chat
    val commandName = botCommand.javaClass.simpleName

    log.info("$commandName: ${chat.id} $userId ${user.username} ${user.firstName} ${chat.title} : ${botCommand.args}")

    addRequest("$commandName@$userId", botCommand.bot, botCommand.message) {
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
