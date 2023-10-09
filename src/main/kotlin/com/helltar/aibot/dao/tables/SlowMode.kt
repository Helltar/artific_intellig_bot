package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table

object SlowMode : Table() {

    val userId = long("id")
    val username = varchar("username", 32).nullable()
    val firstName = varchar("firstName", 64)
    val limit = integer("limit")
    val requests = integer("requests")
    val lastRequestTimestamp = long("lastRequestTimestamp")

    override val primaryKey = PrimaryKey(userId)
}