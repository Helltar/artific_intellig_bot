package com.helltar.aibot.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object GlobalSlowmodeTable : Table() {

    val userId = long("user_id")
    val usageCount = integer("usage_count").default(0)
    val lastUsage = timestamp("last_usage").nullable().default(null)

    override val primaryKey = PrimaryKey(userId)
}
