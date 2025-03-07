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

    private const val DRIVER_CLASS_NAME = "org.postgresql.Driver"
    private const val JDBC_URL_FORMAT = "jdbc:postgresql://%s:%s/%s"

    fun init() {
        val jdbcUrl = JDBC_URL_FORMAT.format(postgresqlHost, postgresqlPort, databaseName)
        val database = Database.connect(jdbcUrl, DRIVER_CLASS_NAME, databaseUser, databasePassword)

        initializeSchema(database)
        initializeSudoersTable(database)
        initializeCommandsStateTable(database)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    private fun initializeSchema(database: Database) = transaction(database) {
        SchemaUtils.create(
            ApiKeysTable, BannedUsersTable, ChatWhitelistTable,
            CommandsStateTable, FilesTable, GlobalSlowmodeTable,
            SlowmodeTable, SudoersTable, PrivacyPoliciesTable,
            ConfigurationsTable
        )
    }

    private fun initializeSudoersTable(database: Database) = transaction(database) {
        SudoersTable.insertIgnore {
            it[userId] = creatorId
            it[username] = "Owner"
            it[datetime] = Instant.now(Clock.systemUTC())
        }
    }

    private fun initializeCommandsStateTable(database: Database) = transaction(database) {
        disableableCommands.forEach { command ->
            CommandsStateTable.insertIgnore {
                it[name] = command
                it[isDisabled] = false
                it[datetime] = Instant.now(Clock.systemUTC())
            }
        }
    }
}
