package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object ApiKeysTable : IntIdTable() {

    val provider = varchar("provider", 40).uniqueIndex()
    val apiKey = varchar("api_key", 150)
    val updatedAt = timestamp("updated_at").nullable()
    val createdAt = timestamp("created_at")
}
