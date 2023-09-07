package com.helltar.artific_intellig_bot.dao

import com.helltar.artific_intellig_bot.dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime

class ChatWhiteList {

    fun add(chatId: Long, title: String?) = dbQuery {
        ChatWhiteListTable.insertIgnore {
            it[this.chatId] = chatId
            it[this.title] = title
            it[datetime] = LocalDateTime.now()
        }
    }
        .insertedCount > 0

    fun remove(chatId: Long) = dbQuery {
        ChatWhiteListTable.deleteWhere { this.chatId eq chatId } > 0
    }

    fun getList() = dbQuery {
        ChatWhiteListTable.selectAll().toList()
    }

    fun isChatExists(chatId: Long) = dbQuery {
        ChatWhiteListTable.select { ChatWhiteListTable.chatId eq chatId }.count() > 0
    }
}