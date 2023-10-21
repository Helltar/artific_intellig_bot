package com.helltar.aibot.dao

import com.helltar.aibot.BotConfig.DIR_DB
import com.helltar.aibot.BotConfig.FILE_DATABASE
import com.helltar.aibot.BotConfig.availableApiProviders
import com.helltar.aibot.commands.Commands.disalableCommandsList
import com.helltar.aibot.dao.tables.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {

    val banList = BanList()
    val sudoers = Sudoers()
    val chatWhiteList = ChatWhiteList()
    val filesIds = FilesIds()
    val commandsState = CommandsState()
    val slowMode = SlowMode()
    val apiKeys = ApiKeys()

    fun init(creatorId: Long) {
        val databaseDir = File(DIR_DB)

        if (!databaseDir.exists() && !databaseDir.mkdir())
            throw RuntimeException("error when create database-dir: $DIR_DB")

        val driver = "org.sqlite.JDBC"
        val url = "jdbc:sqlite:$FILE_DATABASE"
        val database = Database.connect(url, driver)

        transaction(database) {
            SchemaUtils.create(
                ApiKeysTable,
                BanListTable,
                ChatWhiteListTable,
                CommandsStateTable,
                FilesIdsTable,
                SlowModeTable,
                SudoersTable
            )
        }

        setup(creatorId)
    }

    fun <T> dbQuery(block: () -> T): T =
        runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                addLogger(StdOutSqlLogger)
                block()
            }
        }

    private fun setup(creatorId: Long) {
        sudoers.add(creatorId, "Owner")
        availableApiProviders.forEach { apiKeys.add(it, null) }
        disalableCommandsList.forEach { commandsState.add(it) }
    }
}