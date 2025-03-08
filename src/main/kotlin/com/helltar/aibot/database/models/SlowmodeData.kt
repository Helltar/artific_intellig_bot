package com.helltar.aibot.database.models

import java.time.Instant

data class SlowmodeData(
    val userId: Long,
    val username: String?,
    val firstName: String,
    val limit: Int,
    val usageCount: Int,
    val lastUsage: Instant?
)

data class GlobalSlowmodeData(
    val usageCount: Int,
    val lastUsage: Instant?
)

data class SlowmodeStateData(
    val limit: Int,
    val usageCount: Int,
    val lastUsage: Instant?
)
