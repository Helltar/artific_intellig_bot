package com.helltar.artific_intellig_bot.db

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class ChatWhiteList {

    fun add(chatId: Long, title: String = "") = Database.dbQuery {
        ChatWhiteListTable.insertIgnore {
            it[this.chatId] = chatId
            it[this.title] = title
        }
    }
        .insertedCount > 0

    fun remove(chatId: Long) = Database.dbQuery {
        ChatWhiteListTable.deleteWhere { this.chatId eq chatId } > 0
    }

    fun getList() = Database.dbQuery {
        var text = ""

        ChatWhiteListTable.selectAll().forEach {
            val title = it[ChatWhiteListTable.title].run { if (isNotEmpty()) "<i>($this)</i>" else "empty" }
            text += "<code>${it[ChatWhiteListTable.chatId]}</code> $title\n"
        }

        return@dbQuery text
    }

    fun isChatExists(chatId: Long) = Database.dbQuery {
        ChatWhiteListTable.select { ChatWhiteListTable.chatId eq chatId }.count() > 0
    }
}