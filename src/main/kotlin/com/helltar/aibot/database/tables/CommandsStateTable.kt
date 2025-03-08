package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object CommandsStateTable : IntIdTable() {

    val commandName = varchar("command_name", 40).uniqueIndex()
    val isDisabled = bool("is_disabled")
    val updatedAt = timestamp("updated_at").nullable()
    val createdAt = timestamp("created_at")
}
