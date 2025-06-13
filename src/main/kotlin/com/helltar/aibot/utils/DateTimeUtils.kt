package com.helltar.aibot.utils

import java.time.Clock
import java.time.Instant

object DateTimeUtils {

    fun utcNow(): Instant =
        Instant.now(Clock.systemUTC())
}
