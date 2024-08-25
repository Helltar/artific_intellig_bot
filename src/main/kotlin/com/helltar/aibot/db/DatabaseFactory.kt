package com.helltar.aibot.db

import com.helltar.aibot.EnvConfig.creatorId
import com.helltar.aibot.EnvConfig.databaseName
import com.helltar.aibot.EnvConfig.databasePassword
import com.helltar.aibot.EnvConfig.databaseUser
import com.helltar.aibot.EnvConfig.postgresqlHost
import com.helltar.aibot.EnvConfig.postgresqlPort
import com.helltar.aibot.commands.Commands.disableableCommands
import com.helltar.aibot.db.tables.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Clock
import java.time.Instant

object DatabaseFactory {

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
        if (SudoersTable.selectAll().count() == 0L) {
            SudoersTable.insert {
                it[userId] = creatorId
                it[username] = "Owner"
                it[datetime] = Instant.now(Clock.systemUTC())
            }
        }
    }

    private fun initializeCommandsStateTable(database: Database) = transaction(database) {
        if (CommandsStateTable.selectAll().count() == 0L) {
            disableableCommands.forEach { command ->
                CommandsStateTable.insert {
                    it[name] = command
                    it[isDisabled] = false
                    it[datetime] = Instant.now(Clock.systemUTC())
                }
            }
        }
    }
}
