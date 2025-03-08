package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object ChatWhitelistTable : IntIdTable() {

    val chatId = long("chat_id").uniqueIndex()
    val title = varchar("title", 70).nullable()
    val createdAt = timestamp("created_at")
}
