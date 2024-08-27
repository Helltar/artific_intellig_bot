package com.helltar.aibot.bot

import com.helltar.aibot.Config
import com.helltar.aibot.Config.DIR_FILES
import com.helltar.aibot.Config.LOADING_GIF_FILE_NAME
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.db.dao.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import java.io.File
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.hours

class CommandExecutor {

    data class CommandOptions(
        val checkRights: Boolean,
        val isAdminCommand: Boolean,
        val isCreatorCommand: Boolean,
        val isLongRunningCommand: Boolean,
        val privateChatOnly: Boolean
    )

    private companion object {
        const val SLOW_MODE_DURATION_HOURS = 1
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val requestsMap = ConcurrentHashMap<String, Job>()

    private val log = LoggerFactory.getLogger(javaClass)

    fun execute(botCommand: BotCommand, options: CommandOptions) {
        val user = botCommand.ctx.user()
        val chat = botCommand.ctx.message().chat
        val commandName = botCommand.getCommandName()

        logCommandExecution(botCommand, user, chat, commandName)

        val requestKey = "$commandName@${user.id}"

        if (isRequestInProgress(requestKey, botCommand)) return

        requestsMap[requestKey] =
            scope.launch(CoroutineName(requestKey)) {
                if (options.privateChatOnly && !chat.isUserChat) return@launch

                if (!options.checkRights || checkPermissions(botCommand, options) || canExecuteCommand(botCommand))
                    runCommand(botCommand, options.isLongRunningCommand)
            }
    }

    private fun logCommandExecution(botCommand: BotCommand, user: User, chat: Chat, commandName: String) {
        val logMessage = "$commandName: ${chat.id} ${chat.title} ${user.id} ${user.userName} ${user.firstName}: ${botCommand.ctx.message().text}"
        log.info(logMessage)
    }

    private fun isRequestInProgress(requestKey: String, botCommand: BotCommand) =
        if (requestsMap.containsKey(requestKey) && requestsMap[requestKey]?.isCompleted == false) {
            botCommand.replyToMessage(Strings.MANY_REQUEST)
            true
        } else
            false

    private suspend fun checkPermissions(botCommand: BotCommand, options: CommandOptions): Boolean {
        val isCreator = botCommand.ctx.user().id == Config.creatorId
        val isAdmin = botCommand.isAdmin()

        return when {
            options.isCreatorCommand && !isCreator -> false
            options.isAdminCommand && !isAdmin -> false
            isCreator || isAdmin -> true
            else -> false
        }
    }

    private suspend fun canExecuteCommand(botCommand: BotCommand): Boolean {
        val userId = botCommand.ctx.user().id

        if (botCommand.isUserBanned(userId)) {
            val reason = banlistDao.getReason(userId) ?: "\uD83E\uDD37\u200D♂️" // 🤷‍♂️
            botCommand.replyToMessage(Strings.BAN_AND_REASON.format(reason))
            return false
        }

        if (!botCommand.isChatInWhiteList()) {
            botCommand.replyToMessage(Strings.COMMAND_NOT_SUPPORTED_IN_CHAT)
            return false
        }

        val commandName = botCommand.getCommandName()

        if (botCommand.isCommandDisabled(commandName)) {
            botCommand.replyToMessage(Strings.COMMAND_TEMPORARY_DISABLED)
            return false
        }

        return checkSlowMode(botCommand)
    }

    private suspend fun checkSlowMode(botCommand: BotCommand): Boolean {
        if (botCommand.getCommandName() in Commands.disableableCommands) {
            val slowmodeRemainingSeconds = getSlowmodeRemainingSeconds(botCommand.ctx.user())

            if (slowmodeRemainingSeconds > 0) {
                botCommand.replyToMessage(Strings.SLOW_MODE_PLEASE_WAIT.format(slowmodeRemainingSeconds))
                return false
            }
        }

        return true
    }

    private suspend fun runCommand(botCommand: BotCommand, isLongRunningCommand: Boolean) {
        if (isLongRunningCommand) {
            val caption = Strings.localizedString(Strings.CHAT_WAIT_MESSAGE, botCommand.userLanguageCode)
            val messageId = sendWaitingGif(botCommand, caption)

            try {
                botCommand.run()
            } finally {
                botCommand.deleteMessage(messageId)
            }
        } else
            botCommand.run()
    }

    private suspend fun getSlowmodeRemainingSeconds(user: User): Long {
        val slowmodeState =
            slowmodeDao.getSlowmodeState(user.id)
                ?: return getGlobalSlowmodeRemainingSeconds(user.id)

        val limit = slowmodeState.limit
        val lastRequest = slowmodeState.lastRequest
        var requestCount = slowmodeState.requests

        lastRequest?.let {
            val timeElapsed = Duration.between(it, Instant.now(Clock.systemUTC()))

            if (requestCount >= limit && timeElapsed.toHours() < SLOW_MODE_DURATION_HOURS)
                return SLOW_MODE_DURATION_HOURS.hours.inWholeSeconds - timeElapsed.seconds
            else if (timeElapsed.toHours() >= SLOW_MODE_DURATION_HOURS)
                requestCount = 0
        }

        slowmodeDao.update(user, limit, requestCount + 1)

        return 0
    }

    private suspend fun getGlobalSlowmodeRemainingSeconds(userId: Long): Long {
        var slowmodeState = globalSlowmodeDao.getUsageState(userId)

        if (slowmodeState == null) {
            globalSlowmodeDao.add(userId)
            slowmodeState = globalSlowmodeDao.getUsageState(userId) ?: return 0
        }

        var usageCount = slowmodeState.usageCount
        val lastUsage = slowmodeState.lastUsage

        lastUsage?.let {
            val timeElapsed = Duration.between(it, Instant.now(Clock.systemUTC()))
            val globalSlowmodeMaxUsageCount = configurationsDao.getGlobalSlowmodeMaxUsageCount()

            if (usageCount >= globalSlowmodeMaxUsageCount && timeElapsed.toHours() < SLOW_MODE_DURATION_HOURS)
                return SLOW_MODE_DURATION_HOURS.hours.inWholeSeconds - timeElapsed.seconds
            else if (timeElapsed.toHours() >= SLOW_MODE_DURATION_HOURS)
                usageCount = 0
        }

        globalSlowmodeDao.update(userId, usageCount + 1)

        return 0
    }

    private suspend fun sendWaitingGif(botCommand: BotCommand, caption: String): Int {
        val fileName = LOADING_GIF_FILE_NAME

        suspend fun sendGifAndReturnMessageId(): Int {
            val message = botCommand.sendDocument(File("$DIR_FILES/$LOADING_GIF_FILE_NAME"))
            message.document.fileId?.let { filesDao.add(fileName, it) }
            return message.messageId
        }

        return filesDao.getFileId(fileName)?.let { fileId ->
            try {
                botCommand.replyToMessageWithDocument(fileId, caption)
            } catch (e: Exception) {
                log.error(e.message)
                filesDao.delete(fileName)
                sendGifAndReturnMessageId()
            }
        }
            ?: sendGifAndReturnMessageId()
    }
}