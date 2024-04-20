package com.helltar.aibot.commands

import com.helltar.aibot.BotConfig
import com.helltar.aibot.RequestExecutor
import com.helltar.aibot.Strings
import com.helltar.aibot.dao.DatabaseFactory
import com.helltar.aibot.dao.tables.SlowModeTable
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.User
import java.util.concurrent.TimeUnit

class CommandExecutor(private val botConfig: BotConfig.JsonData) {

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

        log.info("$commandName: ${chat.id} $userId ${user.userName} ${user.firstName} ${chat.title} : ${botCommand.ctx.message().text}")

        if (userChatOnly && !chat.isUserChat)
            return

        botCommand.also { bc ->
            if (!checkRights)
                return@also

            val isCreator = userId == botConfig.creatorId
            val isAdmin = bc.isAdmin()

            if (isCreatorCommand && !isCreator)
                return

            if (isAdminCommand && !isAdmin)
                return

            if (isCreator or isAdmin)
                return@also

            if (bc.isUserBanned(userId)) {
                val reason = DatabaseFactory.banList.getReason(userId) ?: "\uD83E\uDD37\u200D♂️" // 🤷‍♂️
                bc.replyToMessage(String.format(Strings.BAN_AND_REASON, reason))
                return
            }

            if (!bc.isChatInWhiteList()) {
                bc.replyToMessage(Strings.COMMAND_NOT_SUPPORTED_IN_CHAT)
                return
            }

            if (bc.isCommandDisabled(commandName)) {
                bc.replyToMessage(Strings.COMMAND_TEMPORARY_DISABLED)
                return
            }

            if (commandName in Commands.disalableCommandsList) {
                val slowModeRemainingSeconds = checkSlowMode(user)

                if (slowModeRemainingSeconds > 0) {
                    bc.replyToMessage(String.format(Strings.SLOW_MODE_PLEASE_WAIT, slowModeRemainingSeconds))
                    return
                }
            }
        }

        val requestKey = "${botCommand.javaClass.simpleName}@$userId"

        if (!RequestExecutor.addRequest(requestKey) { runCommand(botCommand, isLongtimeCommand) }) {
            botCommand.ctx
                .replyToMessage()
                .setText(Strings.MANY_REQUEST)
                .callAsync(botCommand.ctx.sender)
        }
    }

    private fun runCommand(botCommand: BotCommand, isLongtimeCommand: Boolean) {
        botCommand.also { bc ->
            if (isLongtimeCommand) {
                val gifCaption = Strings.localizedString(Strings.CHAT_WAIT_MESSAGE, bc.userLanguageCode)
                val waitMessageId = bc.replyToMessageWithDocument(bc.getLoadingGifFileId(), gifCaption)

                try {
                    bc.run()
                } finally {
                    bc.deleteMessage(waitMessageId)
                }
            } else
                bc.run()
        }
    }

    private fun checkSlowMode(user: User): Long {
        val slowModeState = DatabaseFactory.slowMode.getSlowModeState(user.id) ?: return 0

        var userRequests = slowModeState[SlowModeTable.requests]
        val limit = slowModeState[SlowModeTable.limit]

        if (userRequests >= limit) {
            val timestamp = System.currentTimeMillis()
            val lastRequest = slowModeState[SlowModeTable.lastRequestTimestamp]

            if ((lastRequest + TimeUnit.HOURS.toMillis(1)) > timestamp)
                return TimeUnit.MILLISECONDS.toSeconds((lastRequest + TimeUnit.HOURS.toMillis(1)) - timestamp)
            else
                userRequests = 0
        }

        DatabaseFactory.slowMode.update(user, limit, userRequests + 1)

        return 0
    }
}