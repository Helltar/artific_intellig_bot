package com.helltar.aibot.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object CommandsStateTable : Table() {

    val name = varchar("name", 40)
    val isDisabled = bool("is_disabled")
    val datetime = timestamp("datetime")

    override val primaryKey = PrimaryKey(name)
}