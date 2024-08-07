package com.helltar.aibot.db

import com.helltar.aibot.EnvConfig.creatorId
import com.helltar.aibot.EnvConfig.databaseName
import com.helltar.aibot.EnvConfig.databasePassword
import com.helltar.aibot.EnvConfig.databaseUser
import com.helltar.aibot.EnvConfig.postgresqlHost
import com.helltar.aibot.EnvConfig.postgresqlPort
import com.helltar.aibot.commands.Commands.disalableCommandsList
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

    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://$postgresqlHost:$postgresqlPort/$databaseName"
        val database = Database.connect(jdbcURL, driverClassName, databaseUser, databasePassword)

        transaction(database) {
            SchemaUtils.create(
                ApiKeysTable, BannedUsersTable, ChatWhitelistTable,
                CommandsStateTable, FilesTable, GlobalSlowmodeTable,
                SlowmodeTable, SudoersTable, PrivacyPoliciesTable,
                ConfigurationsTable
            )

            if (SudoersTable.selectAll().count() == 0L) {
                SudoersTable.insert {
                    it[this.userId] = creatorId
                    it[this.username] = "Owner"
                    it[datetime] = Instant.now(Clock.systemUTC())
                }
            }

            if (CommandsStateTable.selectAll().count() == 0L) {
                disalableCommandsList.forEach { command ->
                    CommandsStateTable.insert {
                        it[name] = command
                        it[isDisabled] = false
                        it[datetime] = Instant.now(Clock.systemUTC())
                    }
                }
            }
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}