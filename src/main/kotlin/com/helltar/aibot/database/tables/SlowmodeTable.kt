package com.helltar.aibot.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object SlowmodeTable : Table() {

    val userId = long("user_id")
    val usageCount = integer("usage_count").default(1)
    val updatedAt = timestamp("updated_at")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(userId)
}
