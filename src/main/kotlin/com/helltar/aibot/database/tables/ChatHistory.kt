package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object ChatHistory : IntIdTable() {

    val userId = long("user_id").index()
    val role = varchar("role", 30).index()
    val content = text("content")

    val createdAt = timestamp("created_at")
}
