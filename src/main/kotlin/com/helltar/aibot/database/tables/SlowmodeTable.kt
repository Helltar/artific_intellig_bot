package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object SlowmodeTable : IntIdTable() {

    val userId = long("user_id").uniqueIndex()
    val username = varchar("username", 32).nullable()
    val firstName = varchar("first_name", 64)
    val limit = integer("limit")
    val usageCount = integer("usage_count")
    val updatedAt = timestamp("updated_at").nullable()
    val createdAt = timestamp("created_at")
}
