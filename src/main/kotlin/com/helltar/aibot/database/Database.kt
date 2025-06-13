package com.helltar.aibot.database

import com.helltar.aibot.Config.creatorId
import com.helltar.aibot.Config.databaseName
import com.helltar.aibot.Config.databasePassword
import com.helltar.aibot.Config.databaseUser
import com.helltar.aibot.Config.postgresqlHost
import com.helltar.aibot.Config.postgresqlPort
import com.helltar.aibot.commands.Commands.disableableCommands
import com.helltar.aibot.database.tables.*
import com.helltar.aibot.utils.DateTimeUtils.utcNow
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object Database {

    fun init() {
        val url = "jdbc:postgresql://$postgresqlHost:$postgresqlPort/$databaseName"
        val database = Database.connect(url, "org.postgresql.Driver", databaseUser, databasePassword)

        transaction(database) {
            createTables()
            createSudoUser()
            initializeCommands()
        }
    }

    suspend fun <T> dbTransaction(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    private fun createTables() {
        SchemaUtils.create(
            ApiKeysTable, BannedUsersTable, ChatAllowlistTable,
            CommandsStateTable, SlowmodeTable, SudoersTable, ConfigurationsTable, ChatHistory
        )
    }

    private fun createSudoUser() {
        SudoersTable
            .insertIgnore {
                it[userId] = creatorId
                it[username] = "Owner"
                it[createdAt] = utcNow()
            }
    }

    private fun initializeCommands() {
        disableableCommands.forEach { command ->
            CommandsStateTable
                .insertIgnore { // todo: batchInsert
                    it[commandName] = command
                    it[isDisabled] = false
                    it[createdAt] = utcNow()
                }
        }
    }
}
