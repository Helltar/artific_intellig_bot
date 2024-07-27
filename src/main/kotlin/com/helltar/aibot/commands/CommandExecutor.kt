package com.helltar.aibot.commands

import com.helltar.aibot.EnvConfig
import com.helltar.aibot.Strings
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.DatabaseFactory.FILE_NAME_LOADING_GIF
import com.helltar.aibot.dao.DatabaseFactory.globalSlowmodeDAO
import com.helltar.aibot.dao.DatabaseFactory.slowmodeDAO
import com.helltar.aibot.dao.tables.GlobalSlowmodeTable
import com.helltar.aibot.dao.tables.SlowmodeTable
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.User
import java.io.File
import java.time.Clock
import java.time.Duration
import java.time.Instant
import kotlin.time.Duration.Companion.hours

class CommandExecutor {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val requestsMap = hashMapOf<String, Job>()
    private val log = LoggerFactory.getLogger(javaClass)

    private companion object {
        const val SLOWMODE_BOUNDS_HOURS = 1
    }

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
                val reason = DatabaseFactory.banlistDAO.getReason(userId) ?: "\uD83E\uDD37\u200D♂️" // 🤷‍♂️
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
        val resultRow =
            slowmodeDAO.getSlowmodeState(user.id)
                ?: return getGlobalSlowmodeRemainingSeconds(user.id)

        var requestsCount = resultRow[SlowmodeTable.requests]
        val limit = resultRow[SlowmodeTable.limit]
        val lastRequest = resultRow[SlowmodeTable.lastRequest]

        lastRequest?.let {
            val timeElapsed = Duration.between(it, Instant.now(Clock.systemUTC()))

            if (requestsCount >= limit && timeElapsed.toHours() < SLOWMODE_BOUNDS_HOURS)
                return SLOWMODE_BOUNDS_HOURS.hours.inWholeSeconds - timeElapsed.seconds
            else if (timeElapsed.toHours() >= SLOWMODE_BOUNDS_HOURS)
                requestsCount = 0
        }

        slowmodeDAO.update(user, limit, requestsCount + 1)

        return 0
    }

    private suspend fun getGlobalSlowmodeRemainingSeconds(userId: Long): Long {
        var resultRow = globalSlowmodeDAO.getUsageState(userId)

        if (resultRow == null) {
            globalSlowmodeDAO.add(userId)
            resultRow = globalSlowmodeDAO.getUsageState(userId) ?: return 0
        }

        var usageCount = resultRow[GlobalSlowmodeTable.usageCount]
        val lastUsage = resultRow[GlobalSlowmodeTable.lastUsage]

        lastUsage?.let {
            val timeElapsed = Duration.between(it, Instant.now(Clock.systemUTC()))

            if (usageCount >= 10 && timeElapsed.toHours() < SLOWMODE_BOUNDS_HOURS)
                return SLOWMODE_BOUNDS_HOURS.hours.inWholeSeconds - timeElapsed.seconds
            else if (timeElapsed.toHours() >= SLOWMODE_BOUNDS_HOURS)
                usageCount = 0
        }

        globalSlowmodeDAO.update(userId, usageCount + 1)

        return 0
    }

    private suspend fun sendWaitingGif(botCommand: BotCommand, gifCaption: String): Int {

        suspend fun sendGifAndSaveFileId(): Int {
            val message = botCommand.sendDocument(File("data/files/$FILE_NAME_LOADING_GIF"))
            message.document.fileId?.let { DatabaseFactory.filesDAO.add(FILE_NAME_LOADING_GIF, it) }
            return message.messageId
        }

        return DatabaseFactory.filesDAO.getFileId(FILE_NAME_LOADING_GIF)?.let { fileId ->
            try {
                botCommand.replyToMessageWithDocument(fileId, gifCaption)
            } catch (e: Exception) {
                log.error(e.message)
                DatabaseFactory.filesDAO.delete(FILE_NAME_LOADING_GIF)
                sendGifAndSaveFileId()
            }
        } ?: sendGifAndSaveFileId()
    }
}