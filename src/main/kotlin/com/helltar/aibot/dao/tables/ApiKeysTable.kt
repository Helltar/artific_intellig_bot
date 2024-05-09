package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

enum class ApiKeyType { CREATOR, ADMIN, USER }

object ApiKeysTable : Table() {

    val provider = varchar("provider", 64)
    val apiKey = varchar("apiKey", 128)
    val type = enumeration<ApiKeyType>("type")
    val datetime = datetime("datetime")

    override val primaryKey = PrimaryKey(apiKey)
}