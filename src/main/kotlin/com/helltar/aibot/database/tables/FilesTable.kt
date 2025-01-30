package com.helltar.aibot.database.tables

import org.jetbrains.exposed.sql.Table

object FilesTable : Table() {

    val name = varchar("name", 50)
    val fileId = varchar("file_id", 255)

    override val primaryKey = PrimaryKey(name)
}
