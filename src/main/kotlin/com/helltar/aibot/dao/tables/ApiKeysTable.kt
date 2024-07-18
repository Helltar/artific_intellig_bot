package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ApiKeysTable : Table() {

    val provider = varchar("provider", 40)
    val apiKey = varchar("api_key", 150)
    val added_time = timestamp("added_time")

    override val primaryKey = PrimaryKey(provider)
}