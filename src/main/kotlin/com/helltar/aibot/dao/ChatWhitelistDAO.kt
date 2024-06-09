package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.ChatWhitelistTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import java.time.Clock
import java.time.Instant

class ChatWhitelistDAO {

    suspend fun add(chatId: Long, title: String?) = dbQuery {
        ChatWhitelistTable.insertIgnore {
            it[this.chatId] = chatId
            it[this.title] = title
            it[datetime] = Instant.now(Clock.systemUTC())
        }
    }
        .insertedCount > 0

    suspend fun remove(chatId: Long) = dbQuery {
        ChatWhitelistTable.deleteWhere { this.chatId eq chatId } > 0
    }

    suspend fun getList() = dbQuery {
        ChatWhitelistTable.selectAll().toList()
    }

    suspend fun isChatExists(chatId: Long) = dbQuery {
        ChatWhitelistTable.selectAll().where { ChatWhitelistTable.chatId eq chatId }.count() > 0
    }
}