package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object ConfigurationsTable : IntIdTable() {

    val key = varchar("key", 50).uniqueIndex()
    val value = varchar("value", 250)
    val updatedAt = timestamp("updated_at").nullable()
    val createdAt = timestamp("created_at")
}
