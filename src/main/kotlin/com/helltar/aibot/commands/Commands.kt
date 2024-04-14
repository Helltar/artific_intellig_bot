package com.helltar.aibot.commands

object Commands {

    const val CMD_CHAT = "chat"
    const val CMD_CHATCTX = "chatctx" // dialog context
    const val CMD_CHAT_CTX_REMOVE = "chatrm" // remove context
    const val CMD_GPT_VISION = "vision"

    const val CMD_START = "start"
    const val CMD_DALLE = "dalle"
    const val CMD_SDIFF = "sdif"
    const val CMD_ASR = "asr"
    const val CMD_DALLE_VARIATIONS = "dallevar"

    const val CMD_ABOUT = "about"
    const val CMD_UPTIME = "uptime"
    const val CMD_MYID = "myid"

    const val CMD_ADD_ADMIN = "addadmin"
    const val CMD_RM_ADMIN = "rmadmin"
    const val CMD_ADMIN_LIST = "sudoers"

    const val CMD_BAN_USER = "ban"
    const val CMD_UNBAN_USER = "unban"
    const val CMD_BAN_LIST = "banlist"

    const val CMD_DISABLE = "disable"
    const val CMD_ENABLE = "enable"

    const val CMD_CHATS_WHITE_LIST = "chats"
    const val CMD_ADD_CHAT = "addchat"
    const val CMD_RM_CHAT = "rmchat"

    const val CMD_SLOW_MODE = "slowmode"
    const val CMD_SLOW_MODE_OFF = "slowmodeoff"
    const val CMD_SLOW_MODE_LIST = "slowmodelist"

    const val CMD_UPDATE_API_KEY = "updatekey"

    val disalableCommandsList =
        setOf(
            CMD_CHAT,
            CMD_GPT_VISION,
            CMD_DALLE,
            CMD_SDIFF,
            CMD_ASR,
            CMD_DALLE_VARIATIONS
        )
}