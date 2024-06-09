package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ChatWhitelistTable : Table() {

    val chatId = long("chat_id")
    val title = varchar("title", 100).nullable()
    val datetime = timestamp("datetime")

    override val primaryKey = PrimaryKey(chatId)
}