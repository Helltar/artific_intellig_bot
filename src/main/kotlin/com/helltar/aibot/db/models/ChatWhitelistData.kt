package com.helltar.aibot.db.models

import java.time.Instant

data class ChatWhitelistData(
    val chatId: Long,
    val title: String?,
    val datetime: Instant
)
