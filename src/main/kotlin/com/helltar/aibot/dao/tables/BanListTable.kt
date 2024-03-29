package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object BanListTable : Table() {

    val userId = long("id")
    val username = varchar("username", 32).nullable()
    val firstName = varchar("firstName", 64)
    val reason = varchar("reason", 128).nullable()
    val datetime = datetime("datetime")

    override val primaryKey = PrimaryKey(userId)
}