package com.helltar.artific_intellig_bot.db

import com.github.kotlintelegrambot.entities.User
import com.helltar.artific_intellig_bot.db.BanList.firstName
import com.helltar.artific_intellig_bot.db.BanList.reason
import com.helltar.artific_intellig_bot.db.BanList.userId
import com.helltar.artific_intellig_bot.db.BanList.username
import com.helltar.artific_intellig_bot.db.Database.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class BanListTable {

    fun banUser(user: User, reason: String) = dbQuery {
        BanList.insertIgnore {
            it[userId] = user.id
            it[username] = user.username ?: ""
            it[firstName] = user.firstName
            it[this.reason] = reason
        }
    }

    fun unbanUser(userId: Long) = dbQuery {
        BanList.deleteWhere { this.userId eq userId }
    }

    fun isUserBanned(userId: Long) = dbQuery {
        BanList.select { BanList.userId eq userId }.count() > 0
    }

    fun getList(): List<String> = dbQuery {
        val result = arrayListOf<String>()

        BanList.selectAll().forEach {
            val username = it[username].run { if (isNotEmpty()) "@$this" else "<b>${it[firstName]}</b>" }
            val reason = it[reason].run { if (isNotEmpty()) "<i>($this)</i>" else "" }
            result.add("<code>${it[userId]}</code> $username $reason")
        }

        return@dbQuery result
    }
}
