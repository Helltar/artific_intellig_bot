package com.helltar.artific_intellig_bot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ChatWhiteList : Table() {

    val chatId = long("id")
    val title = varchar("title", 64).nullable()
    val datetime = datetime("datetime")

    override val primaryKey = PrimaryKey(chatId)
}