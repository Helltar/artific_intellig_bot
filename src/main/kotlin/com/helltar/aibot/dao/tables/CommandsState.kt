package com.helltar.aibot.dao.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object CommandsState : Table() {

    val name = varchar("name", 32)
    val isDisabled = bool("isDisabled")
    val datetime = datetime("datetime")

    override val primaryKey = PrimaryKey(name)
}