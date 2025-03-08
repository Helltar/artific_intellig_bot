package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object SudoersTable : IntIdTable() {

    val userId = long("user_id").uniqueIndex()
    val username = varchar("username", 32).nullable()
    val createdAt = timestamp("created_at")
}
