package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.ChatWhitelistTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime

class ChatWhitelistDAO {

    fun add(chatId: Long, title: String?) = dbQuery {
        ChatWhitelistTable.insertIgnore {
            it[this.chatId] = chatId
            it[this.title] = title
            it[datetime] = LocalDateTime.now()
        }
    }
        .insertedCount > 0

    fun remove(chatId: Long) = dbQuery {
        ChatWhitelistTable.deleteWhere { this.chatId eq chatId } > 0
    }

    fun getList() = dbQuery {
        ChatWhitelistTable.selectAll().toList()
    }

    fun isChatExists(chatId: Long) = dbQuery {
        ChatWhitelistTable.select { ChatWhitelistTable.chatId eq chatId }.count() > 0
    }
}