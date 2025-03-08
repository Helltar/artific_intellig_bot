package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.models.ChatWhitelistData
import com.helltar.aibot.database.tables.ChatWhitelistTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll

class ChatWhitelistDao {

    suspend fun add(chatId: Long, title: String?): Boolean = dbTransaction {
        ChatWhitelistTable
            .insertIgnore {
                it[this.chatId] = chatId
                it[this.title] = title?.take(70)
                it[createdAt] = utcNow()
            }
    }
        .insertedCount > 0

    suspend fun remove(chatId: Long): Boolean = dbTransaction {
        ChatWhitelistTable
            .deleteWhere { this.chatId eq chatId } > 0
    }

    suspend fun list(): List<ChatWhitelistData> = dbTransaction {
        ChatWhitelistTable
            .selectAll()
            .map {
                ChatWhitelistData(
                    it[ChatWhitelistTable.chatId],
                    it[ChatWhitelistTable.title],
                    it[ChatWhitelistTable.createdAt],
                )
            }
    }

    suspend fun isExists(chatId: Long): Boolean = dbTransaction {
        ChatWhitelistTable
            .select(ChatWhitelistTable.chatId)
            .where { ChatWhitelistTable.chatId eq chatId }
            .empty().not()
    }
}

val chatWhitelistDao = ChatWhitelistDao()
