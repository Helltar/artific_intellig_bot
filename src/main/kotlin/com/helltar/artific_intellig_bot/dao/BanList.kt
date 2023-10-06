package com.helltar.artific_intellig_bot.dao

import com.helltar.artific_intellig_bot.dao.DatabaseFactory.dbQuery
import com.helltar.artific_intellig_bot.dao.tables.BanList.reason
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.telegram.telegrambots.meta.api.objects.User
import java.time.LocalDateTime
import com.helltar.artific_intellig_bot.dao.tables.BanList as BanListTable

class BanList {

    fun banUser(user: User, reason: String?) = dbQuery {
        BanListTable.insertIgnore {
            it[userId] = user.id
            it[username] = user.userName
            it[firstName] = user.firstName
            it[this.reason] = reason
            it[datetime] = LocalDateTime.now()
        }
            .insertedCount > 0
    }

    fun unbanUser(userId: Long) = dbQuery {
        BanListTable.deleteWhere { this.userId eq userId } > 0
    }

    fun isUserBanned(userId: Long) = dbQuery {
        BanListTable.select { BanListTable.userId eq userId }.count() > 0
    }

    fun getReason(userId: Long) = dbQuery {
        BanListTable.select { BanListTable.userId eq userId }.single()[reason]
    }

    fun getList() = dbQuery {
        BanListTable.selectAll().toList()
    }
}