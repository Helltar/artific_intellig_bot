package com.helltar.aibot.database

import com.helltar.aibot.commands.Commands.disableableCommands
import com.helltar.aibot.config.Config.creatorId
import com.helltar.aibot.config.Config.databaseName
import com.helltar.aibot.config.Config.databasePassword
import com.helltar.aibot.config.Config.databaseUser
import com.helltar.aibot.config.Config.postgresqlHost
import com.helltar.aibot.config.Config.postgresqlPort
import com.helltar.aibot.database.tables.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Clock
import java.time.Instant

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

    fun utcNow(): Instant =
        Instant.now(Clock.systemUTC())

    private fun createTables() {
        SchemaUtils.create(
            ApiKeysTable, BannedUsersTable, ChatWhitelistTable,
            CommandsStateTable, FilesTable, GlobalSlowmodeTable,
            SlowmodeTable, SudoersTable, ConfigurationsTable
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
