package com.helltar.aibot.database.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object ConfigurationsTable : Table() {

    val key = varchar("key", 50)
    val value = varchar("value", 250)
    val updatedAt = timestamp("updated_at").nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(key)
}
