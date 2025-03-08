package com.helltar.aibot.bot

import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Config
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.dao.*
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
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
        const val DIR_FILES = "data/files"
        const val LOADING_GIF_FILE_NAME = "loading.gif"
        const val SLOW_MODE_DURATION_HOURS = 1
        val log = KotlinLogging.logger {}
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val requestsMap = ConcurrentHashMap<String, Job>()

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
        log.info { logMessage }
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
            val reason = banlistDao.reason(userId) ?: "\uD83E\uDD37\u200D♂️" // 🤷‍♂️
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
        try {
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
        } catch (e: Exception) {
            log.error { e.message }
        }
    }

    private suspend fun getSlowmodeRemainingSeconds(user: User): Long {
        val slowmodeData =
            slowmodeDao.userSlowmodeData(user.id)
                ?: return getGlobalSlowmodeRemainingSeconds(user.id)

        val limit = slowmodeData.limit
        val lastUsage = slowmodeData.lastUsage
        var usageCount = slowmodeData.usageCount

        lastUsage?.let {
            val timeElapsed = Duration.between(it, Instant.now(Clock.systemUTC()))

            if (usageCount >= limit && timeElapsed.toHours() < SLOW_MODE_DURATION_HOURS)
                return SLOW_MODE_DURATION_HOURS.hours.inWholeSeconds - timeElapsed.seconds
            else if (timeElapsed.toHours() >= SLOW_MODE_DURATION_HOURS)
                slowmodeDao.resetUsageCount(user.id)
        }

        slowmodeDao.incrementUsageCount(user.id)

        return 0
    }

    private suspend fun getGlobalSlowmodeRemainingSeconds(userId: Long): Long {
        val slowmodeData = globalSlowmodeDao.userSlowmodeData(userId)

        if (slowmodeData == null) {
            globalSlowmodeDao.add(userId)
            globalSlowmodeDao.incrementUsageCount(userId)
            return 0
        }

        var usageCount = slowmodeData.usageCount
        val lastUsage = slowmodeData.lastUsage

        lastUsage?.let {
            val timeElapsed = Duration.between(it, Instant.now(Clock.systemUTC()))
            val globalSlowmodeMaxUsageCount = configurationsDao.getGlobalSlowmodeMaxUsageCount()

            if (usageCount >= globalSlowmodeMaxUsageCount && timeElapsed.toHours() < SLOW_MODE_DURATION_HOURS)
                return SLOW_MODE_DURATION_HOURS.hours.inWholeSeconds - timeElapsed.seconds
            else if (timeElapsed.toHours() >= SLOW_MODE_DURATION_HOURS)
                globalSlowmodeDao.resetUsageCount(userId)
        }

        globalSlowmodeDao.incrementUsageCount(userId)

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
                log.error { e.message }
                filesDao.delete(fileName)
                sendGifAndReturnMessageId()
            }
        }
            ?: sendGifAndReturnMessageId()
    }
}
