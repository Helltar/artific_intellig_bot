package com.helltar.aibot.bot

import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.base.BotCommand
import com.helltar.aibot.config.Config
import com.helltar.aibot.config.Config.LOADING_GIF_FILENAME
import com.helltar.aibot.config.Strings
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.dao.banlistDao
import com.helltar.aibot.database.dao.configurationsDao
import com.helltar.aibot.database.dao.slowmodeDao
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import java.io.File
import java.time.Duration
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
        const val SLOW_MODE_TIMEOUT_HOURS = 1
        val log = KotlinLogging.logger {}
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val requestsMap = ConcurrentHashMap<String, Job>()

    fun execute(botCommand: BotCommand, options: CommandOptions) {
        val user = botCommand.ctx.user()
        val userId = user.id
        val chat = botCommand.ctx.message().chat
        val commandName = botCommand.getCommandName()

        logCommandExecution(botCommand, user, chat, commandName)

        val requestKey = "$commandName@$userId"

        if (isRequestInProgress(requestKey, botCommand)) return

        requestsMap[requestKey] =
            scope.launch {
                if (options.privateChatOnly && !chat.isUserChat) return@launch

                try {
                    val isCreator = userId == Config.creatorId
                    val isAdmin = botCommand.isAdmin()

                    val shouldRunCommand =
                        when {
                            !options.checkRights -> true
                            options.isCreatorCommand -> isCreator
                            options.isAdminCommand -> isAdmin
                            isCreator || isAdmin -> true
                            else -> isCanExecuteCommand(botCommand)
                        }

                    if (shouldRunCommand)
                        runCommand(botCommand, options.isLongRunningCommand)
                    else
                        log.info { "command /$commandName not allowed for user $userId" }
                } catch (e: Exception) {
                    log.error { e.message }
                } finally {
                    requestsMap.remove(requestKey)
                }
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

    private suspend fun isCanExecuteCommand(botCommand: BotCommand): Boolean {
        val userId = botCommand.ctx.user().id

        if (botCommand.isUserBanned(userId)) {
            val reason = banlistDao.reason(userId) ?: "\uD83E\uDD37\u200D♂️" // 🤷‍♂️
            botCommand.replyToMessage(Strings.BAN_AND_REASON.format(reason))
            return false
        }

        if (!botCommand.isChatInAllowlistList()) {
            botCommand.replyToMessage(Strings.COMMAND_NOT_SUPPORTED_IN_CHAT)
            return false
        }

        val commandName = botCommand.getCommandName()

        if (botCommand.isCommandDisabled(commandName)) {
            botCommand.replyToMessage(Strings.COMMAND_TEMPORARY_DISABLED)
            return false
        }

        return isNotInSlowmode(botCommand)
    }

    private suspend fun isNotInSlowmode(botCommand: BotCommand): Boolean {
        if (botCommand.getCommandName() in Commands.disableableCommands) {
            val slowmodeRemainingSeconds = getSlowmodeRemainingSeconds(botCommand.ctx.user().id)

            if (slowmodeRemainingSeconds > 0) {
                botCommand.replyToMessage(Strings.SLOWMODE_PLEASE_WAIT.format(slowmodeRemainingSeconds))
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

    private suspend fun getSlowmodeRemainingSeconds(userId: Long): Long {
        val userSlowmodeStatus = slowmodeDao.slowmodeStatus(userId)

        if (userSlowmodeStatus == null) {
            slowmodeDao.registerUser(userId)
            return 0
        }

        val lastUsage = userSlowmodeStatus.lastUsage
        val timeElapsed = Duration.between(lastUsage, utcNow())

        if (timeElapsed.toHours() >= SLOW_MODE_TIMEOUT_HOURS) {
            slowmodeDao.resetUsageCount(userId)
            return 0
        }

        val slowmodeMaxUsageCount = configurationsDao.getSlowmodeMaxUsageCount()

        if (userSlowmodeStatus.usageCount >= slowmodeMaxUsageCount)
            return SLOW_MODE_TIMEOUT_HOURS.hours.inWholeSeconds - timeElapsed.seconds

        slowmodeDao.incrementUsageCount(userId)

        return 0
    }

    private suspend fun sendWaitingGif(botCommand: BotCommand, caption: String): Int {

        suspend fun sendLoadingGifAndUpdateFileId(): Int {
            val message = botCommand.sendDocument(File(LOADING_GIF_FILENAME))
            message.document.fileId?.let { configurationsDao.updateLoadingGifFileId(it) }
            return message.messageId
        }

        return configurationsDao.getLoadingGifFileId()?.let { fileId ->
            try {
                botCommand.replyToMessageWithDocument(fileId, caption)
            } catch (e: Exception) {
                log.error { e.message }
                sendLoadingGifAndUpdateFileId()
            }
        }
            ?: sendLoadingGifAndUpdateFileId()
    }
}
