package com.helltar.aibot.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object BannedUsersTable : Table() {

    val userId = long("user_id")
    val username = varchar("username", 32).nullable()
    val firstName = varchar("first_name", 64)
    val reason = varchar("reason", 150).nullable()
    val datetime = timestamp("datetime")

    override val primaryKey = PrimaryKey(userId)
}
