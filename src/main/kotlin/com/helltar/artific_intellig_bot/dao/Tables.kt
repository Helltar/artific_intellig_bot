package com.helltar.artific_intellig_bot.dao

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

object SudoersTable : Table() {
    val userId = long("id")
    val username = varchar("username", 32).nullable()
    val datetime = datetime("datetime")
    override val primaryKey = PrimaryKey(userId)
}

object ChatWhiteListTable : Table() {
    val chatId = long("id")
    val title = varchar("title", 64).nullable()
    val datetime = datetime("datetime")
    override val primaryKey = PrimaryKey(chatId)
}

object FilesIdsTable : Table() {
    val name = varchar("name", 64)
    val fileId = varchar("fileId", 255)
    override val primaryKey = PrimaryKey(name)
}

object CommandsStateTable : Table() {
    val name = varchar("name", 32)
    val isDisabled = bool("isDisabled")
    val datetime = datetime("datetime")
    override val primaryKey = PrimaryKey(name)
}

object SlowModeTable : Table() {
    val userId = long("id")
    val username = varchar("username", 32).nullable()
    val firstName = varchar("firstName", 64)
    val limit = integer("limit")
    val requests = integer("requests")
    val lastRequestTimestamp = long("lastRequestTimestamp")
    override val primaryKey = PrimaryKey(userId)
}