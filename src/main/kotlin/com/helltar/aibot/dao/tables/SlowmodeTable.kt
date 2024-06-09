package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object SlowmodeTable : Table() {

    val userId = long("user_id")
    val username = varchar("username", 32).nullable()
    val firstName = varchar("first_name", 64)
    val limit = integer("limit")
    val requests = integer("requests_count")
    val lastRequest = timestamp("last_request").nullable()

    override val primaryKey = PrimaryKey(userId)
}