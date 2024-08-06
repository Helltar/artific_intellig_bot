package com.helltar.aibot.db.models

import java.time.Instant

data class SudoersData(
    val userId: Long,
    val username: String?,
    val datetime: Instant
)
