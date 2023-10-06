package com.helltar.artific_intellig_bot.dao.tables

import org.jetbrains.exposed.sql.Table

object FilesIds : Table() {

    val name = varchar("name", 64)
    val fileId = varchar("fileId", 255)

    override val primaryKey = PrimaryKey(name)
}