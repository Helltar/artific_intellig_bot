package com.helltar.aibot.database.models

import java.time.Instant

data class SlowmodeStatusData(
    val usageCount: Int,
    val lastUsage: Instant?
)
