package com.helltar.artific_intellig_bot.dao

import com.helltar.artific_intellig_bot.FILE_DATABASE
import com.helltar.artific_intellig_bot.dao.tables.BanList
import com.helltar.artific_intellig_bot.dao.tables.ChatWhiteList
import com.helltar.artific_intellig_bot.dao.tables.CommandsState
import com.helltar.artific_intellig_bot.dao.tables.FilesIds
import com.helltar.artific_intellig_bot.dao.tables.SlowMode
import com.helltar.artific_intellig_bot.dao.tables.Sudoers
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
    val slowMode = SlowMode()

    fun init() {
        val driver = "org.sqlite.JDBC"
        val url = "jdbc:sqlite:$FILE_DATABASE"
        val database = Database.connect(url, driver)
        transaction(database) { SchemaUtils.create(BanList, Sudoers, ChatWhiteList, FilesIds, CommandsState, SlowMode) }
    }

    fun <T> dbQuery(block: () -> T): T =
        runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                addLogger(StdOutSqlLogger)
                block()
            }
        }
}