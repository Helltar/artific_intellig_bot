package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object BannedUsersTable : IntIdTable() {

    val userId = long("user_id").uniqueIndex()
    val username = varchar("username", 32).nullable()
    val firstName = varchar("first_name", 64)
    val reason = varchar("reason", 150).nullable()
    val bannedAt = timestamp("banned_at")
}
