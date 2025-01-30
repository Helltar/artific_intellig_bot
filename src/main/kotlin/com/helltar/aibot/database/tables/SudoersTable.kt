package com.helltar.aibot.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object SudoersTable : Table() {

    val userId = long("user_id")
    val username = varchar("username", 32).nullable()
    val datetime = timestamp("datetime")

    override val primaryKey = PrimaryKey(userId)
}
