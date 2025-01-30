package com.helltar.aibot.commands

import com.helltar.aibot.commands.Commands.User.CMD_ASR
import com.helltar.aibot.commands.Commands.User.CMD_CHAT
import com.helltar.aibot.commands.Commands.User.CMD_DALLE
import com.helltar.aibot.commands.Commands.User.CMD_DALLE_VARIATIONS
import com.helltar.aibot.commands.Commands.User.CMD_GPT_VISION

object Commands {

    object Simple {
        const val CMD_ABOUT = "about"
        const val CMD_MYID = "myid"
        const val CMD_PRIVACY = "privacy"
        const val CMD_START = "start"
    }

    object User {
        const val CMD_ASR = "asr"
        const val CMD_CHAT = "chat"
        const val CMD_CHATCTX = "chatctx" // dialog context
        const val CMD_CHAT_CTX_REMOVE = "chatrm" // remove context
        const val CMD_DALLE = "dalle"
        const val CMD_DALLE_VARIATIONS = "dallevar"
        const val CMD_GPT_VISION = "vision"
    }

    object Creator {
        const val CMD_ADD_ADMIN = "addadmin"
        const val CMD_ADD_CHAT = "addchat"
        const val CMD_DEEP_SEEK_OFF = "deepseekoff"
        const val CMD_DEEP_SEEK_ON = "deepseekon"
        const val CMD_GLOBAL_SLOW_MODE = "globalslowmode"
        const val CMD_UPDATE_API_KEY = "updatekey"
        const val CMD_UPDATE_PRIVACY_POLICY = "updateprivacy"
    }

    object Admin {
        const val CMD_ADMIN_LIST = "sudoers"
        const val CMD_BAN_LIST = "banlist"
        const val CMD_BAN_USER = "ban"
        const val CMD_CHATS_WHITE_LIST = "chats"
        const val CMD_DISABLE = "disable"
        const val CMD_ENABLE = "enable"
        const val CMD_RM_ADMIN = "rmadmin"
        const val CMD_RM_CHAT = "rmchat"
        const val CMD_SLOW_MODE = "slowmode"
        const val CMD_SLOW_MODE_LIST = "slowmodelist"
        const val CMD_SLOW_MODE_OFF = "slowmodeoff"
        const val CMD_UNBAN_USER = "unban"

    }

    val disableableCommands =
        setOf(
            CMD_CHAT,
            CMD_GPT_VISION,
            CMD_DALLE,
            CMD_ASR,
            CMD_DALLE_VARIATIONS
        )
}
