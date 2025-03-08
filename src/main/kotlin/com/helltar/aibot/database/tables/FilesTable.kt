package com.helltar.aibot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object FilesTable : IntIdTable() {

    val fileName = varchar("file_name", 50).uniqueIndex()
    val fileId = varchar("file_id", 255)
    val updatedAt = timestamp("updated_at").nullable()
    val createdAt = timestamp("created_at")
}
