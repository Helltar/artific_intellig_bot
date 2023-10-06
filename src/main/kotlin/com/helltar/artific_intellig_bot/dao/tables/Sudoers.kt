package com.helltar.artific_intellig_bot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Sudoers : Table() {

    val userId = long("id")
    val username = varchar("username", 32).nullable()
    val datetime = datetime("datetime")

    override val primaryKey = PrimaryKey(userId)
}