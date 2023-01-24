package com.helltar.artific_intellig_bot.commands

object Commands {

    const val commandChat = "chat"
    const val commandDalle = "dalle"
    const val commandStableDiffusion = "sdif"

    const val commandEnable = "enable"
    const val commandDisable = "disable"
    const val commandChatAsText = "chat_as_text"
    const val commandChatAsVoice = "chat_as_voice"
    const val commandBanUser = "ban_user"
    const val commandUnbanUser = "unban_user"
    const val commandBanList = "ban_list"

    const val commandAbout = "about"

    val commandsList = listOf(commandChat, commandDalle, commandStableDiffusion)
}
