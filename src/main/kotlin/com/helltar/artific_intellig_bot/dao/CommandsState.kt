package com.helltar.artific_intellig_bot.dao

import com.helltar.artific_intellig_bot.dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import com.helltar.artific_intellig_bot.dao.tables.CommandsState as CommandsStateTable

class CommandsState {

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