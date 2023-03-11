package com.helltar.artific_intellig_bot.db

import com.helltar.artific_intellig_bot.db.BanListTable.reason
import com.helltar.artific_intellig_bot.db.Database.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.telegram.telegrambots.meta.api.objects.User

class BanList {

    fun banUser(user: User, reason: String) = dbQuery {
        BanListTable.insertIgnore {
            it[userId] = user.id
            it[username] = user.userName ?: ""
            it[firstName] = user.firstName
            it[this.reason] = reason
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
        var text = ""

        BanListTable.selectAll().forEach {
            val username = it[BanListTable.username].ifEmpty { it[BanListTable.firstName] }
            val reason = it[reason].run { if (isNotEmpty()) "<i>($this)</i>" else "" }
            text += "<code>${it[BanListTable.userId]}</code> <b>$username</b> $reason\n"
        }

        return@dbQuery text
    }
}
