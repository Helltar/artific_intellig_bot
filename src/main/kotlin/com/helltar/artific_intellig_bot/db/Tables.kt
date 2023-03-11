package com.helltar.artific_intellig_bot.db

import org.jetbrains.exposed.sql.Table

object BanListTable : Table() {
    val userId = long("id")
    val username = varchar("username", 32)
    val firstName = varchar("firstName", 64)
    val reason = varchar("reason", 100)
    override val primaryKey = PrimaryKey(userId)
}

object SudoersTable : Table() {
    val userId = long("id")
    val username = varchar("username", 32)
    override val primaryKey = PrimaryKey(userId)
}

object ChatWhiteListTable : Table() {
    val chatId = long("id")
    val title = varchar("title", 64)
    override val primaryKey = PrimaryKey(chatId)
}