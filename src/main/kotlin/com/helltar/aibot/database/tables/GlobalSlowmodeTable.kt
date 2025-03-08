package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object GlobalSlowmodeTable : IntIdTable() {

    val userId = long("user_id").uniqueIndex()
    val usageCount = integer("usage_count").default(0)
    val updatedAt = timestamp("updated_at").nullable()
    val createdAt = timestamp("created_at")
}
