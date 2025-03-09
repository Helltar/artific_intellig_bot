package com.helltar.aibot.database.models

import java.time.Instant

data class ChatAllowlistData(
    val chatId: Long,
    val title: String?,
    val createdAt: Instant
)
