package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.tables.ChatHistory
import com.helltar.aibot.openai.ApiConfig.ChatRole
import com.helltar.aibot.openai.models.common.MessageData
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import java.time.Instant

class ChatHistoryDao {

    suspend fun insert(userId: Long, mesasage: MessageData): Boolean = dbTransaction {
        ChatHistory
            .insert {
                it[this.userId] = userId
                it[role] = mesasage.role
                it[content] = mesasage.content
                it[createdAt] = utcNow()
            }.insertedCount > 0
    }

    suspend fun loadHistory(userId: Long): List<Pair<MessageData, Instant>> = dbTransaction {
        ChatHistory
            .select(ChatHistory.role, ChatHistory.content, ChatHistory.createdAt)
            .where { ChatHistory.userId eq userId }
            .orderBy(ChatHistory.id)
            .map {
                MessageData(
                    it[ChatHistory.role],
                    it[ChatHistory.content]
                ) to it[ChatHistory.createdAt]
            }
    }

    suspend fun deleteOldestEntry(userId: Long): Boolean = dbTransaction {
        val messageId =
            ChatHistory
                .select(ChatHistory.id)
                .where { ChatHistory.userId eq userId and (ChatHistory.role neq ChatRole.SYSTEM) }
                .orderBy(ChatHistory.id)
                .limit(1)
                .singleOrNull()?.getOrNull(ChatHistory.id)

        messageId?.let { ChatHistory.deleteWhere { ChatHistory.id eq messageId } > 0 } == true
    }

    suspend fun clearHistory(userId: Long): Boolean = dbTransaction {
        ChatHistory
            .deleteWhere { ChatHistory.userId eq userId } > 0
    }
}

val chatHistoryDao = ChatHistoryDao()
