package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table

object FilesIdsTable : Table() {

    val name = varchar("name", 64)
    val fileId = varchar("fileId", 255)

    override val primaryKey = PrimaryKey(name)
}