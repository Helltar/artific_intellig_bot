package com.helltar.aibot

import com.helltar.aibot.BotConfig.DIR_LOCALE
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.util.regex.Pattern

object Strings {

    const val BAD_REQUEST = "<code>Bad Request</code> \uD83D\uDE10" // 😐
    const val BAN_AND_REASON = "❌ Ban, reason: <b>%s</b>"
    const val CHAT_AS_TEXT_ALREADY_ENABLED = "✅ ChatAsText already enabled"
    const val CHAT_AS_TEXT_OK = "✅ ChatAsText"
    const val CHAT_AS_VOICE_ALREADY_ENABLED = "✅ ChatAsVoice already enabled"
    const val CHAT_AS_VOICE_OK = "✅ ChatAsVoice"
    const val CHAT_EXCEPTION = "Something is broken \uD83D\uDE48" // 🙈
    const val CHAT_HELLO = "\uD83D\uDC4B Hello, please ask your questions as replying to my messages" // 👋
    const val CHAT_CONTEXT_REMOVED = "Context has been removed \uD83D\uDC4C" // 👌
    const val CHAT_CONTEXT_REMOVED_INFO = "Try again (context has been removed) ℹ\uFE0F" // ℹ️
    const val CHAT_CONTEXT_EMPTY = "▫\uFE0F Empty"
    const val COMMAND_ALREADY_DISABLED = "✅ Command <b>%s</b> already disabled"
    const val COMMAND_ALREADY_ENABLED = "✅ Command <b>%s</b> already enabled"
    const val COMMAND_DISABLED = "✅ Command <b>%s</b> disabled"
    const val COMMAND_ENABLED = "✅ Command <b>%s</b> enabled"
    const val COMMAND_NOT_AVAILABLE = "Command <b>%s</b> not available: %s"
    const val COMMAND_NOT_SUPPORTED_IN_CHAT = "Command is not supported in this chat \uD83D\uDE48" // 🙈
    const val COMMAND_TEMPORARY_DISABLED = "Command temporary disabled \uD83D\uDC40" // 👀
    const val EMPTY_ARGS = "Please write a description of what you want to receive:\n\n<code>/dalle photo realistic portrait of young woman</code>"
    const val LIST_IS_EMPTY = "◻️ List is empty"
    const val MANY_CHARACTERS = "Max <b>%d</b> characters \uD83D\uDC40" // 👀
    const val MANY_REQUEST = "Wait, let me deal with the last request \uD83E\uDD16" // 🤖
    const val STABLE_DIFFUSION_EMPTY_ARGS = "Please write a description of what you want to receive:\n\n<code>/sdif photo realistic portrait of young woman</code>"
    const val IMAGE_MUST_BE_LESS_THAN = "Image must be less than %s 😥" // 😥

    const val USER_ALREADY_BANNED = "✅ User already banned"
    const val USER_BANNED = "❌ User banned"
    const val USER_NOT_BANNED = "✅ User not banned"
    const val USER_UNBANNED = "✅ User unbanned"

    const val ADMIN_ADDED = "✅ Admin added"
    const val ADMIN_EXISTS = "✅ Admin already exists"
    const val ADMIN_REMOVED = "✅ Admin has been removed"
    const val ADMIN_NOT_EXISTS = "❌ Admin does not exist"

    const val CHAT_ADDED = "✅ Chat added"
    const val CHAT_EXISTS = "✅ Chat already exists"
    const val CHAT_REMOVED = "✅ Chat has been removed"
    const val CHAT_NOT_EXISTS = "❌ Chat does not exist"

    const val CHAT_GPT_SYSTEM_MESSAGE = "chat_gpt_system_message"
    const val CHAT_WAIT_MESSAGE = "chat_wait_message"

    const val CREATOR_ONLY_COMMAND = "You cannot use this command like this (admin-only) ✋" // ✋

    const val SLOW_MODE_PLEASE_WAIT = "✋ Slow mode, wait <b>%d</b> seconds"
    const val SLOW_MODE_ON = "✅ Slow mode on, <b>%d</b> requests per hour"
    const val SLOW_MODE_ON_UPDATE = "\uD83D\uDD04 Slow mode already on, update, <b>%d</b> requests per hour" // 🔄
    const val SLOW_MODE_OFF = "✅ Slow mode off"
    const val SLOW_MODE_OFF_NOT_ENABLED = "ℹ\uFE0F Slow mode not enabled for this user" // ℹ️

    private val log = LoggerFactory.getLogger(Strings.javaClass)

    fun localizedString(key: String, languageCode: String): String {

        fun getFirstRegexGroup(text: String, regex: String): String {
            val m = Pattern.compile(regex).matcher(text)

            return if (m.find()) {
                if (m.groupCount() >= 1) m.group(1) else ""
            } else ""
        }

        return try {
            var filename = "$DIR_LOCALE/${languageCode.lowercase()}.xml"

            if (!File(filename).exists())
                filename = "$DIR_LOCALE/en.xml"

            val regex = """<string name="$key">(\X*?)<\/string>"""
            getFirstRegexGroup(FileReader(filename).readText(), regex).trimIndent().ifEmpty { key }
        } catch (e: Exception) {
            log.error(e.message)
            key
        }
    }
}