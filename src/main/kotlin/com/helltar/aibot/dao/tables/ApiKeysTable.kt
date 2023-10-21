package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ApiKeysTable : Table() {

    val provider = varchar("provider", 64)
    val apiKey = varchar("apiKey", 128).nullable()
    val datetime = datetime("datetime")

    override val primaryKey = PrimaryKey(provider)
}