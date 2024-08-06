package com.helltar.aibot.db.tables

import org.jetbrains.exposed.sql.Table

object ConfigurationsTable : Table() {

    val key = varchar("key", 50)
    val value = varchar("value", 255)

    override val primaryKey = PrimaryKey(key)
}