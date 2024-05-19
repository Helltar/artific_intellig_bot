package com.helltar.aibot.dao

import com.helltar.aibot.BotConfig.DIR_DB
import com.helltar.aibot.BotConfig.FILENAME_DATABASE
import com.helltar.aibot.BotConfig.creatorId
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

    val apiKeyDAO = ApiKeyDAO()
    val banListDAO = BanListDAO()
    val chatWhitelistDAO = ChatWhitelistDAO()
    val commandsDAO = CommandsDAO()
    val filesDAO = FilesDAO()
    val slowmodeDAO = SlowmodeDAO()
    val sudoersDAO = SudoersDAO()

    fun init() {
        val databaseDir = File(DIR_DB)

        if (!databaseDir.exists() && !databaseDir.mkdirs())
            throw RuntimeException("error when create database-dir: $DIR_DB")

        val driver = "org.sqlite.JDBC"
        val url = "jdbc:sqlite:$FILENAME_DATABASE"
        val database = Database.connect(url, driver)

        transaction(database) {
            SchemaUtils.create(
                ApiKeysTable,
                BannedUsersTable,
                ChatWhitelistTable,
                CommandsStateTable,
                FilesTable,
                SlowmodeTable,
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
        sudoersDAO.add(creatorId, "Owner")
        disalableCommandsList.forEach { commandsDAO.add(it) }
    }
}