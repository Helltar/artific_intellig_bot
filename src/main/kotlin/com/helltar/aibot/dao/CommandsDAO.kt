package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.CommandsStateTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Clock
import java.time.Instant

class CommandsDAO {

    suspend fun changeState(command: String, disable: Boolean) = dbQuery {
        CommandsStateTable.update({ CommandsStateTable.name eq command }) {
            it[name] = command
            it[isDisabled] = disable
            it[datetime] = Instant.now(Clock.systemUTC())
        }
    }

    suspend fun isDisabled(command: String) = dbQuery {
        CommandsStateTable.selectAll().where { CommandsStateTable.name eq command }.singleOrNull()?.get(CommandsStateTable.isDisabled) ?: false
    }
}