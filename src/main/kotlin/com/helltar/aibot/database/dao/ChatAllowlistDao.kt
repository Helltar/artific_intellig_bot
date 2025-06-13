package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.models.ChatAllowlistData
import com.helltar.aibot.database.tables.ChatAllowlistTable
import com.helltar.aibot.utils.DateTimeUtils.utcNow
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll

class ChatAllowlistDao {

    suspend fun add(chatId: Long, title: String?): Boolean = dbTransaction {
        ChatAllowlistTable
            .insertIgnore {
                it[this.chatId] = chatId
                it[this.title] = title?.take(70)
                it[createdAt] = utcNow()
            }
    }
        .insertedCount > 0

    suspend fun remove(chatId: Long): Boolean = dbTransaction {
        ChatAllowlistTable
            .deleteWhere { this.chatId eq chatId } > 0
    }

    suspend fun list(): List<ChatAllowlistData> = dbTransaction {
        ChatAllowlistTable
            .selectAll()
            .map {
                ChatAllowlistData(
                    it[ChatAllowlistTable.chatId],
                    it[ChatAllowlistTable.title],
                    it[ChatAllowlistTable.createdAt],
                )
            }
    }

    suspend fun isExists(chatId: Long): Boolean = dbTransaction {
        ChatAllowlistTable
            .select(ChatAllowlistTable.chatId)
            .where { ChatAllowlistTable.chatId eq chatId }
            .empty()
            .not()
    }
}

val chatAllowlistDao = ChatAllowlistDao()
