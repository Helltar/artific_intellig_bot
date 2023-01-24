package com.helltar.artific_intellig_bot.commands

object Commands {

    const val cmdChat = "chat"
    const val cmdDalle = "dalle"
    const val cmdStableDiffusion = "sdif"

    const val cmdEnable = "enable"
    const val cmdDisable = "disable"
    const val cmdChatAsText = "chat_as_text"
    const val cmdChatAsVoice = "chat_as_voice"
    const val cmdBanUser = "ban_user"
    const val cmdUnbanUser = "unban_user"
    const val cmdBanList = "ban_list"

    const val cmdAbout = "about"

    val disalableCmdsList = listOf(cmdChat, cmdDalle, cmdStableDiffusion)
}
