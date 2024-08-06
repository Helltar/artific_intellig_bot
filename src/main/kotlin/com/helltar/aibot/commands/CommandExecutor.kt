package com.helltar.aibot.commands

import com.helltar.aibot.EnvConfig
import com.helltar.aibot.Strings
import com.helltar.aibot.db.dao.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.User
import java.io.File
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.hours

class CommandExecutor {

    private companion object {
        const val SLOWMODE_BOUNDS_HOURS = 1
        const val FILE_NAME_LOADING_GIF = "loading.gif"
        val filesMap = mapOf(FILE_NAME_LOADING_GIF to "data/files/$FILE_NAME_LOADING_GIF")
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val requestsMap = ConcurrentHashMap<String, Job>()

    private val log = LoggerFactory.getLogger(javaClass)

    fun execute(
        botCommand: BotCommand,
        isAdminCommand: Boolean = false,
        isCreatorCommand: Boolean = false,
        checkRights: Boolean = true,
        userChatOnly: Boolean = false,
        isLongtimeCommand: Boolean = false
    ) {
        val user = botCommand.ctx.user()
        val userId = user.id
        val chat = botCommand.ctx.message().chat
        val commandName = botCommand.getCommandName()

        val logMessage = "$commandName: ${chat.id} $userId ${user.userName} ${user.firstName} ${chat.title}: ${botCommand.ctx.message().text}"
        log.info(logMessage)

        val requestKey = "$commandName@$userId"

        if (requestsMap.containsKey(requestKey)) {
            if (requestsMap[requestKey]?.isCompleted == false) {
                botCommand.replyToMessage(Strings.MANY_REQUEST)
                return
            }
        }

        requestsMap[requestKey] = scope.launch(CoroutineName(requestKey)) {
            if (userChatOnly && !chat.isUserChat)
                return@launch

            if (!checkRights) {
                runCommand(botCommand, isLongtimeCommand)
                return@launch
            }

            val isCreator = userId == EnvConfig.creatorId

            if (isCreatorCommand && !isCreator)
                return@launch

            val isAdmin = botCommand.isAdmin()

            if (isAdminCommand && !isAdmin)
                return@launch

            if (isCreator or isAdmin) {
                runCommand(botCommand, isLongtimeCommand)
                return@launch
            }

            if (botCommand.isUserBanned(userId)) {
                val reason = banlistDao.getReason(userId) ?: "\uD83E\uDD37\u200D♂️" // 🤷‍♂️
                botCommand.replyToMessage(Strings.BAN_AND_REASON.format(reason))
                return@launch
            }

            if (!botCommand.isChatInWhiteList()) {
                botCommand.replyToMessage(Strings.COMMAND_NOT_SUPPORTED_IN_CHAT)
                return@launch
            }

            if (botCommand.isCommandDisabled(commandName)) {
                botCommand.replyToMessage(Strings.COMMAND_TEMPORARY_DISABLED)
                return@launch
            }

            if (commandName in Commands.disalableCommandsList) {
                val slowmodeRemainingSeconds = getSlowmodeRemainingSeconds(user)

                if (slowmodeRemainingSeconds > 0) {
                    botCommand.replyToMessage(Strings.SLOW_MODE_PLEASE_WAIT.format(slowmodeRemainingSeconds))
                    return@launch
                }
            }

            runCommand(botCommand, isLongtimeCommand)
        }
    }

    private suspend fun runCommand(command: BotCommand, isLongtimeCommand: Boolean) {
        if (isLongtimeCommand) {
            val gifCaption = Strings.localizedString(Strings.CHAT_WAIT_MESSAGE, command.userLanguageCode)
            val waitMessageId = sendWaitingGif(command, gifCaption)

            try {
                command.run()
            } finally {
                command.deleteMessage(waitMessageId)
            }
        } else
            command.run()
    }

    private suspend fun getSlowmodeRemainingSeconds(user: User): Long {
        val slowmodeState =
            slowmodeDao.getSlowmodeState(user.id)
                ?: return getGlobalSlowmodeRemainingSeconds(user.id)

        val limit = slowmodeState.limit
        val lastRequest = slowmodeState.lastRequest
        var requestsCount = slowmodeState.requests

        lastRequest?.let {
            val timeElapsed = Duration.between(it, Instant.now(Clock.systemUTC()))

            if (requestsCount >= limit && timeElapsed.toHours() < SLOWMODE_BOUNDS_HOURS)
                return SLOWMODE_BOUNDS_HOURS.hours.inWholeSeconds - timeElapsed.seconds
            else if (timeElapsed.toHours() >= SLOWMODE_BOUNDS_HOURS)
                requestsCount = 0
        }

        slowmodeDao.update(user, limit, requestsCount + 1)

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

            if (usageCount >= globalSlowmodeMaxUsageCount && timeElapsed.toHours() < SLOWMODE_BOUNDS_HOURS)
                return SLOWMODE_BOUNDS_HOURS.hours.inWholeSeconds - timeElapsed.seconds
            else if (timeElapsed.toHours() >= SLOWMODE_BOUNDS_HOURS)
                usageCount = 0
        }

        globalSlowmodeDao.update(userId, usageCount + 1)

        return 0
    }

    private suspend fun sendWaitingGif(botCommand: BotCommand, gifCaption: String): Int {

        suspend fun sendGifAndSaveFileId(): Int {
            val message = botCommand.sendDocument(File(filesMap.getValue(FILE_NAME_LOADING_GIF)))
            message.document.fileId?.let { filesDao.add(FILE_NAME_LOADING_GIF, it) }
            return message.messageId
        }

        return filesDao.getFileId(FILE_NAME_LOADING_GIF)?.let { fileId ->
            try {
                botCommand.replyToMessageWithDocument(fileId, gifCaption)
            } catch (e: Exception) {
                log.error(e.message)
                filesDao.delete(FILE_NAME_LOADING_GIF)
                sendGifAndSaveFileId()
            }
        } ?: sendGifAndSaveFileId()
    }
}