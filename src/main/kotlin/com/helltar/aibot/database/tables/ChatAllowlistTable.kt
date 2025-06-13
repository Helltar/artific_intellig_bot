package com.helltar.aibot.database.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object ChatAllowlistTable : Table() {

    val chatId = long("chat_id")
    val title = varchar("title", 70).nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(chatId)
}
