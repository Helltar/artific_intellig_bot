package com.helltar.aibot.database.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object SudoersTable : Table() {

    val userId = long("user_id")
    val username = varchar("username", 32).nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(userId)
}
