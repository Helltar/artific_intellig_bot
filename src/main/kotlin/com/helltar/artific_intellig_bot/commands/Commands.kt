package com.helltar.artific_intellig_bot.commands

object Commands {

    const val cmdChat = "/chat"
    const val cmdDalle = "/dalle"
    const val cmdSDiff = "/sdif"
    const val cmdDalleVariations = "/dallevariations"

    const val cmdAbout = "/about"
    const val cmdUptime = "/uptime"

    const val cmdAddAdmin = "/addadmin"
    const val cmdAdminList = "/sudoers"
    const val cmdBanList = "/banlist"
    const val cmdBanUser = "/ban"
    const val cmdChatAsText = "/chatastext"
    const val cmdChatAsVoice = "/chatasvoice"
    const val cmdDisable = "/disable"
    const val cmdEnable = "/enable"
    const val cmdRmAdmin = "/rmadmin"
    const val cmdUnbanUser = "/unban"

    const val cmdChatWhiteList = "/chats"
    const val cmdAddChat = "/addchat"
    const val cmdRmChat = "/rmchat"

    val disalableCmdsList = listOf(cmdChat, cmdDalle, cmdSDiff, cmdDalleVariations)
}
