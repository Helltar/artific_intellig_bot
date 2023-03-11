package com.helltar.artific_intellig_bot.db

import com.helltar.artific_intellig_bot.FILE_DATABASE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database as SQLDatabase

object Database {

    val banList = BanList()
    val sudoers = Sudoers()
    val chatWhiteList = ChatWhiteList()

    fun init() {
        transaction(SQLDatabase.connect("jdbc:sqlite:$FILE_DATABASE", "org.sqlite.JDBC")) {
            SchemaUtils.create(BanListTable, SudoersTable, ChatWhiteListTable)
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
