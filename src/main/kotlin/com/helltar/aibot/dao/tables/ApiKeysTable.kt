package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

enum class ApiKeyType { CREATOR, ADMIN, USER }

object ApiKeysTable : Table() {

    val provider = varchar("provider", 40)
    val apiKey = varchar("key", 150)
    val type = enumeration<ApiKeyType>("type")
    val datetime = timestamp("datetime")

    override val primaryKey = PrimaryKey(apiKey)
}