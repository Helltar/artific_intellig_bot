package com.helltar.aibot.database.models

import java.time.Instant

data class BanlistData(
    val userId: Long,
    val username: String?,
    val firstName: String,
    val reason: String?,
    val bannedAt: Instant
)
