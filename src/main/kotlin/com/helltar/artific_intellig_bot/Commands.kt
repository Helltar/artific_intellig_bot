package com.helltar.artific_intellig_bot

object Commands {

    // todo: refact.
    const val cmdChatAsVoiceName = "chatasvoice"
    const val cmdChatAsTextName = "chatastext"

    const val cmdChat = "/chat"
    const val cmdChatCtx = "/chatctx" // dialog context
    const val cmdChatCtxRemove = "/chatrm" // remove context
    const val cmdChatAsText = "/$cmdChatAsTextName"
    const val cmdChatAsVoice = "/$cmdChatAsVoiceName"

    const val cmdStart = "/start"
    const val cmdDalle = "/dalle"
    const val cmdSDiff = "/sdif"
    const val cmdDalleVariations = "/dallevariations"

    const val cmdAbout = "/about"
    const val cmdUptime = "/uptime"

    const val cmdAddAdmin = "/addadmin"
    const val cmdRmAdmin = "/rmadmin"
    const val cmdAdminList = "/sudoers"

    const val cmdBanUser = "/ban"
    const val cmdUnbanUser = "/unban"
    const val cmdBanList = "/banlist"

    const val cmdDisable = "/disable"
    const val cmdEnable = "/enable"

    const val cmdChatWhiteList = "/chats"
    const val cmdAddChat = "/addchat"
    const val cmdRmChat = "/rmchat"

    // todo: substring
    val disalableCmdsList =
        listOf(
            cmdChat.substring(1),
            cmdDalle.substring(1),
            cmdSDiff.substring(1),
            cmdDalleVariations.substring(1)
        )
}