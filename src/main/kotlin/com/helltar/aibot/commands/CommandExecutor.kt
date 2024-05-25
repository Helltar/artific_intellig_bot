package com.helltar.aibot.commands

import com.helltar.aibot.BotConfig.creatorId
import com.helltar.aibot.RequestExecutor
import com.helltar.aibot.Strings
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.SlowmodeTable
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.User
import java.util.concurrent.TimeUnit

class CommandExecutor {

    private val log = LoggerFactory.getLogger(javaClass)

    fun execute( // todo: -> BotCommand (?)
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

        val logMessage = "$commandName: ${chat.id} $userId ${user.userName} ${user.firstName} ${chat.title} : ${botCommand.ctx.message().text}"
        log.info(logMessage)

        if (userChatOnly && !chat.isUserChat)
            return

        if (!checkRights) {
            addRequest(botCommand, isLongtimeCommand)
            return
        }

        val isCreator = userId == creatorId

        if (isCreatorCommand && !isCreator)
            return

        val isAdmin = botCommand.isAdmin()

        if (isAdminCommand && !isAdmin)
            return

        if (isCreator or isAdmin) {
            addRequest(botCommand, isLongtimeCommand)
            return
        }

        if (botCommand.isUserBanned(userId)) {
            val reason = DatabaseFactory.banListDAO.getReason(userId) ?: "\uD83E\uDD37\u200D♂️" // 🤷‍♂️
            botCommand.replyToMessage(Strings.BAN_AND_REASON.format(reason))
            return
        }

        if (!botCommand.isChatInWhiteList()) {
            botCommand.replyToMessage(Strings.COMMAND_NOT_SUPPORTED_IN_CHAT)
            return
        }

        if (botCommand.isCommandDisabled(commandName)) {
            botCommand.replyToMessage(Strings.COMMAND_TEMPORARY_DISABLED)
            return
        }

        if (commandName in Commands.disalableCommandsList) {
            val slowmodeRemainingSeconds = checkSlowmode(user)

            if (slowmodeRemainingSeconds > 0) {
                botCommand.replyToMessage(Strings.SLOW_MODE_PLEASE_WAIT.format(slowmodeRemainingSeconds))
                return
            }
        }

        addRequest(botCommand, isLongtimeCommand)
    }

    private fun addRequest(command: BotCommand, isLongtimeCommand: Boolean) {
        val requestKey = "${command.javaClass.simpleName}@${command.ctx.user().id}"

        if (!RequestExecutor.addRequest(requestKey) { runCommand(command, isLongtimeCommand) }) {
            command.ctx
                .replyToMessage()
                .setText(Strings.MANY_REQUEST)
                .callAsync(command.ctx.sender)
        }
    }

    private fun runCommand(command: BotCommand, isLongtimeCommand: Boolean) {
        if (isLongtimeCommand) {
            val gifCaption = Strings.localizedString(Strings.CHAT_WAIT_MESSAGE, command.userLanguageCode)
            val waitMessageId = command.replyToMessageWithDocument(command.getLoadingGifFileId(), gifCaption)

            try {
                command.run()
            } finally {
                command.deleteMessage(waitMessageId)
            }
        } else
            command.run()
    }

    private fun checkSlowmode(user: User): Long {
        val slowModeState = DatabaseFactory.slowmodeDAO.getSlowModeState(user.id) ?: return 0

        var userRequests = slowModeState[SlowmodeTable.requests]
        val limit = slowModeState[SlowmodeTable.limit]

        if (userRequests >= limit) {
            val timestamp = System.currentTimeMillis()
            val lastRequest = slowModeState[SlowmodeTable.lastRequestTimestamp]

            if ((lastRequest + TimeUnit.HOURS.toMillis(1)) > timestamp)
                return TimeUnit.MILLISECONDS.toSeconds((lastRequest + TimeUnit.HOURS.toMillis(1)) - timestamp)
            else
                userRequests = 0
        }

        DatabaseFactory.slowmodeDAO.update(user, limit, userRequests + 1)

        return 0
    }
}