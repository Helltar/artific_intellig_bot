package com.helltar.artific_intellig_bot.dao

import com.helltar.artific_intellig_bot.FILE_DATABASE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    val banList = BanList()
    val sudoers = Sudoers()
    val chatWhiteList = ChatWhiteList()
    val filesIds = FilesIds()
    val commandsState = CommandsState()

    fun init() {
        val driver = "org.sqlite.JDBC"
        val url = "jdbc:sqlite:$FILE_DATABASE"
        val database = Database.connect(url, driver)

        transaction(database) {
            SchemaUtils.create(BanListTable, SudoersTable, ChatWhiteListTable, FilesIdsTable, CommandsStateTable)
        }
    }

    fun <T> dbQuery(block: () -> T): T =
        runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                addLogger(StdOutSqlLogger)
                block()
            }
        }
}