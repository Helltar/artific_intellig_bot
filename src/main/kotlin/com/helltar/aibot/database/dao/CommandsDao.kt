package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.tables.CommandsStateTable
import org.jetbrains.exposed.sql.update

class CommandsDao {

    suspend fun changeState(command: String, disable: Boolean): Int = dbTransaction {
        CommandsStateTable
            .update({ CommandsStateTable.commandName eq command }) {
                it[isDisabled] = disable
                it[updatedAt] = utcNow()
            }
    }

    suspend fun isDisabled(command: String): Boolean = dbTransaction {
        CommandsStateTable
            .select(CommandsStateTable.isDisabled)
            .where { CommandsStateTable.commandName eq command }
            .singleOrNull()?.getOrNull(CommandsStateTable.isDisabled) == true
    }
}

val commandsDao = CommandsDao()
