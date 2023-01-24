package com.helltar.artific_intellig_bot.db

import org.jetbrains.exposed.sql.Table

object BanList : Table() {
    val userId = long("id")
    val username = varchar("username", 32)
    val firstName = varchar("firstName", 64)
    val reason = varchar("reason", 100)
    override val primaryKey = PrimaryKey(userId)
}
