package com.helltar.aibot.database.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object ApiKeysTable : Table() {

    val provider = varchar("provider", 40)
    val apiKey = varchar("api_key", 150)
    val updatedAt = timestamp("updated_at").nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(provider)
}
