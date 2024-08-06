package com.helltar.aibot.db.dao

import com.helltar.aibot.db.DatabaseFactory.dbQuery
import com.helltar.aibot.db.models.ChatWhitelistData
import com.helltar.aibot.db.tables.ChatWhitelistTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import java.time.Clock
import java.time.Instant

class ChatWhitelistDao {

    suspend fun add(chatId: Long, title: String?) = dbQuery {
        ChatWhitelistTable
            .insertIgnore {
                it[this.chatId] = chatId
                it[this.title] = title
                it[datetime] = Instant.now(Clock.systemUTC())
            }
    }
        .insertedCount > 0

    suspend fun remove(chatId: Long) = dbQuery {
        ChatWhitelistTable.deleteWhere { this.chatId eq chatId } > 0
    }

    suspend fun getList(): List<ChatWhitelistData> = dbQuery {
        ChatWhitelistTable
            .selectAll()
            .map {
                ChatWhitelistData(
                    it[ChatWhitelistTable.chatId],
                    it[ChatWhitelistTable.title],
                    it[ChatWhitelistTable.datetime],
                )
            }
    }

    suspend fun isChatExists(chatId: Long) = dbQuery {
        ChatWhitelistTable
            .select(ChatWhitelistTable.chatId)
            .where { ChatWhitelistTable.chatId eq chatId }
            .count() > 0
    }
}

val chatWhitelistDao = ChatWhitelistDao()