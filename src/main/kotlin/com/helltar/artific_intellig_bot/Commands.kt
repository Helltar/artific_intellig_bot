package com.helltar.artific_intellig_bot

object Commands {

    const val cmdChat = "chat"
    const val cmdChatCtx = "chatctx" // dialog context
    const val cmdChatCtxRemove = "chatrm" // remove context
    const val cmdChatAsText = "chatastext"
    const val cmdChatAsVoice = "chatasvoice"

    const val cmdStart = "start"
    const val cmdDalle = "dalle"
    const val cmdSDiff = "sdif"
    const val cmdDalleVariations = "dallevariations"

    const val cmdAbout = "about"
    const val cmdUptime = "uptime"
    const val cmdMyId = "myid"

    const val cmdAddAdmin = "addadmin"
    const val cmdRmAdmin = "rmadmin"
    const val cmdAdminList = "sudoers"

    const val cmdBanUser = "ban"
    const val cmdUnbanUser = "unban"
    const val cmdBanList = "banlist"

    const val cmdDisable = "disable"
    const val cmdEnable = "enable"

    const val cmdChatWhiteList = "chats"
    const val cmdAddChat = "addchat"
    const val cmdRmChat = "rmchat"

    const val cmdSlowMode = "slowmode"
    const val cmdSlowModeOff = "slowmodeoff"
    const val cmdSlowModeList = "slowmodelist"

    val disalableCmdsList =
        setOf(
            cmdChat,
            cmdDalle,
            cmdSDiff,
            cmdDalleVariations
        )
}