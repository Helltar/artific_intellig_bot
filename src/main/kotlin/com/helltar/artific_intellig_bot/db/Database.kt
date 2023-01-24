package com.helltar.artific_intellig_bot.db

import com.helltar.artific_intellig_bot.DATABASE_FILE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database as SQLDatabase

object Database {

    val banListTable = BanListTable()

    fun init() {
        transaction(SQLDatabase.connect("jdbc:sqlite:$DATABASE_FILE", "org.sqlite.JDBC")) {
            SchemaUtils.create(BanList)
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
