package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object SlowmodeTable : IntIdTable() {

    val userId = long("user_id").uniqueIndex()
    val usageCount = integer("usage_count").default(1)
    val updatedAt = timestamp("updated_at")
    val createdAt = timestamp("created_at")
}
