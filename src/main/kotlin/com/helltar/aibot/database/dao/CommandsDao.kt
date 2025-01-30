package com.helltar.aibot.database.dao

import com.helltar.aibot.database.DatabaseFactory.dbQuery
import com.helltar.aibot.database.tables.CommandsStateTable
import org.jetbrains.exposed.sql.update
import java.time.Clock
import java.time.Instant

class CommandsDao {

    suspend fun changeState(command: String, disable: Boolean) = dbQuery {
        CommandsStateTable
            .update({ CommandsStateTable.name eq command }) {
                it[name] = command
                it[isDisabled] = disable
                it[datetime] = Instant.now(Clock.systemUTC())
            }
    }

    suspend fun isDisabled(command: String) = dbQuery {
        CommandsStateTable
            .select(CommandsStateTable.isDisabled)
            .where { CommandsStateTable.name eq command }
            .singleOrNull()?.get(CommandsStateTable.isDisabled) ?: false
    }
}

val commandsDao = CommandsDao()
