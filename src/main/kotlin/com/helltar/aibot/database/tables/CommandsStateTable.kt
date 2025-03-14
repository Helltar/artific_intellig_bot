package com.helltar.aibot.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object CommandsStateTable : Table() {

    val commandName = varchar("command_name", 40)
    val isDisabled = bool("is_disabled")
    val updatedAt = timestamp("updated_at").nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(commandName)
}
