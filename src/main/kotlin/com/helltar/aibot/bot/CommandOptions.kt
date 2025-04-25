package com.helltar.aibot.bot

data class CommandOptions(
    val checkRights: Boolean,
    val isAdminCommand: Boolean,
    val isCreatorCommand: Boolean,
    val isLongRunningCommand: Boolean,
    val privateChatOnly: Boolean
)
