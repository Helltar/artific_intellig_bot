package com.helltar.aibot.config

import com.annimon.tgbotsmodule.services.ResourceBundleLocalizationService
import com.helltar.aibot.commands.Commands.Admin.CMD_SLOW_MODE
import com.helltar.aibot.commands.Commands.Creator.CMD_GLOBAL_SLOW_MODE
import com.helltar.aibot.commands.Commands.Creator.CMD_UPDATE_API_KEY
import com.helltar.aibot.commands.Commands.Creator.CMD_UPDATE_PRIVACY_POLICY
import com.helltar.aibot.commands.Commands.Simple.CMD_PRIVACY
import com.helltar.aibot.commands.Commands.User.CMD_CHAT
import com.helltar.aibot.commands.Commands.User.CMD_CHAT_CTX_REMOVE
import com.helltar.aibot.config.Config.API_KEY_PROVIDER_DEEPSEEK

object Strings {

    const val API_KEY_FAIL_ADD = "⚠\uFE0F Error when add <b>%s</b> API Key" // ⚠️
    const val API_KEY_FAIL_UPDATE = "⚠\uFE0F Error when update <b>%s</b> API Key" // ⚠️
    const val ASR_WHISPER_USE_AS_REPLY = "Use this command as a reply to an voice or video message \uD83D\uDE43" // 🙃
    const val AUDIO_MUST_BE_LESS_THAN = "Audio must be less than %s 😥" // 😥
    const val BAD_API_KEY_LENGTH = "❌ Invalid API key length"
    const val BAD_REQUEST = "Bad Request \uD83D\uDE10" // 😐
    const val BAD_VIDEO_DURATION = "Bad video duration, must be less than <b>%d</b> seconds \uD83D\uDE13" // 😓
    const val BAD_VIDEO_NOTE_DURATION = "Bad video-note duration, must be less than <b>%d</b> seconds \uD83D\uDE13" // 😓
    const val BAN_AND_REASON = "❌ Ban, reason: <b>%s</b>"
    const val CHAT_CONTEXT_EMPTY = "▫\uFE0F Empty"
    const val CHAT_CONTEXT_REMOVED = "Context has been removed \uD83D\uDC4C" // 👌
    const val CHAT_EXCEPTION = "Something is broken \uD83E\uDEE1" // 🫡
    const val CHAT_HELLO = "\uD83D\uDC4B Hello, please ask your questions as replying to my messages" // 👋
    const val COMMAND_ALREADY_DISABLED = "✅ Command <b>%s</b> already disabled"
    const val COMMAND_ALREADY_ENABLED = "✅ Command <b>%s</b> already enabled"
    const val COMMAND_DISABLED = "✅ Command <b>%s</b> disabled"
    const val COMMAND_ENABLED = "✅ Command <b>%s</b> enabled"
    const val DEEPSEEK_ENABLED = "✅ DeepSeek enabled for /$CMD_CHAT"
    const val DEEPSEEK_DISABLED = "✅ DeepSeek disabled"
    const val COMMAND_NOT_AVAILABLE = "Command <b>%s</b> is not available. Available: %s"
    const val COMMAND_NOT_SUPPORTED_IN_CHAT = "Command is not supported in this chat \uD83D\uDE48" // 🙈
    const val COMMAND_TEMPORARY_DISABLED = "Command temporary disabled \uD83D\uDC40" // 👀
    const val COULDNT_RECOGNIZE_VOICE = "Couldn't recognize the voice \uD83D\uDE49" // 🙉
    const val DALLE_VARIATIONS_USE_AS_REPLY = "Use this command as a reply to photo \uD83D\uDE43" // 🙃
    const val EMPTY_ARGS = "Please write a description of what you want to receive:\n\n<code>/dalle photo realistic portrait of young woman</code>"
    const val ERROR_RETRIEVING_AUDIO_FROM_VIDEO = "Error when retrieving audio from a video file \uD83E\uDEE1" // 🫡
    const val GPT_VISION_NO_PHOTO_IN_MESSAGE = "No photo in message \uD83E\uDD14" // 🤔
    const val GPT_VISION_USE_AS_REPLY = "Use this command as a reply to photo \uD83D\uDE43" // 🙃
    const val IMAGE_MUST_BE_LESS_THAN = "Image must be less than %s 😥" // 😥
    const val LIST_IS_EMPTY = "◻️ List is empty"
    const val MANY_CHARACTERS = "Max <b>%d</b> characters \uD83D\uDC40" // 👀
    const val MANY_REQUEST = "Wait, let me deal with the last request \uD83E\uDD16" // 🤖
    const val MESSAGE_TEXT_NOT_FOUND = "The message does not contain text \uD83E\uDD14" // 🤔
    const val NO_PHOTO_IN_MESSAGE = "No photo in message \uD83E\uDD14" // 🤔
    const val PROVIDER_API_KEY_SUCCESS_ADD = "✅ API Key for <b>%s</b> succesfully added"
    const val PROVIDER_API_KEY_SUCCESS_UPDATE = "✅ API Key for <b>%s</b> succesfully updated"
    const val TELEGRAM_API_EXCEPTION_CONTEXT_SAVED_TO_FILE = "An error occurred (TelegramApiException). The context has been saved to a file. You can try fixing it using the /$CMD_CHAT_CTX_REMOVE command ☺\uFE0F"
    const val TELEGRAM_API_EXCEPTION_RESPONSE_SAVED_TO_FILE = "TelegramApiException, response saved to file \uD83D\uDE48" // 🙈
    const val UPDATE_API_KEYS_COMMAND_EXAMPLE = "ℹ\uFE0F Example: <code>/$CMD_UPDATE_API_KEY sk-key_here</code> for <b>OpenAI</b>, <code>/$CMD_UPDATE_API_KEY $API_KEY_PROVIDER_DEEPSEEK sk-key_here</code> for <b>DeepSeek</b>" // ℹ️
    const val VIDEO_OR_AUDIO_NOT_FOUND = "Message does not contain audio or video \uD83E\uDD14" // 🤔
    const val VOICE_MUST_BE_LESS_THAN = "Voice must be less than %s 😥" // 😥

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

    const val ADMIN_ONLY_COMMAND = "You cannot use this command like this (admin-only) ✋" // ✋
    const val CREATOR_CONTEXT_CANNOT_BE_VIEWED = "Creator context cannot be viewed ✋" // ✋
    const val CREATOR_CONTEXT_CANNOT_BE_DELETED = "Creator context cannot be deleted ✋" // ✋

    const val SLOW_MODE_PLEASE_WAIT = "✋ Slow mode, wait <b>%d</b> seconds"
    const val SLOW_MODE_ON = "✅ Slow mode on, <b>%d</b> requests per hour"
    const val SLOW_MODE_ON_UPDATE = "\uD83D\uDD04 Slow mode already on, update, <b>%d</b> requests per hour" // 🔄
    const val SLOW_MODE_OFF = "✅ Slow mode off"
    const val SLOW_MODE_OFF_NOT_ENABLED = "ℹ\uFE0F Slow mode not enabled for this user" // ℹ️
    const val SLOW_MODE_BAD_ARG = "\uD83E\uDD2D Bad args., example: <code>/$CMD_SLOW_MODE 123456789 2</code> (userid, limit), or if this is a reply: <code>/$CMD_SLOW_MODE 2</code>" // 🤭
    const val SLOW_MODE_USER_NOT_FOUND = "\uD83E\uDD37\u200D♂\uFE0F User with this ID is not in the database"

    const val GLOBAL_SLOW_MODE_CURRENT_VALUE = "ℹ\uFE0F The current value is <b>%d</b> requests per hour per user.\n\nTo change it, use the command <code>/$CMD_GLOBAL_SLOW_MODE 15</code>"
    const val GLOBAL_SLOW_MODE_SUCCESFULLY_CHANGED = "✅ The value for global-slowmode has been successfully changed to <b>%d</b> requests per hour per user."

    const val UPDATE_PRIVACY_POLICY_EXAMPLE = "<code>/$CMD_UPDATE_PRIVACY_POLICY</code> text"
    const val PRIVACY_POLICY_SUCCESFULLY_UPDATED = "✅ Privacy Policy succesfully updated, to view use /$CMD_PRIVACY"

    private val localization = ResourceBundleLocalizationService("language")

    fun localizedString(key: String, languageCode: String): String =
        localization.getString(key, languageCode)
}
