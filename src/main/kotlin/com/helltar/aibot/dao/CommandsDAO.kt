package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.CommandsStateTable
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class CommandsDAO {

    fun add(command: String, disable: Boolean = false) = dbQuery {
        CommandsStateTable.insertIgnore {
            it[name] = command
            it[isDisabled] = disable
            it[datetime] = LocalDateTime.now()
        }
    }

    fun changeState(command: String, disable: Boolean) = dbQuery {
        CommandsStateTable.update({ CommandsStateTable.name eq command }) {
            it[name] = command
            it[isDisabled] = disable
            it[datetime] = LocalDateTime.now()
        }
    }

    fun isDisabled(command: String) = dbQuery {
        CommandsStateTable.select { CommandsStateTable.name eq command }.singleOrNull()?.get(CommandsStateTable.isDisabled) ?: false
    }
}