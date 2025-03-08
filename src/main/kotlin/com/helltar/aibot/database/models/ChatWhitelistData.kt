package com.helltar.aibot.database.models

import java.time.Instant

data class ChatWhitelistData(
    val chatId: Long,
    val title: String?,
    val createdAt: Instant
)
