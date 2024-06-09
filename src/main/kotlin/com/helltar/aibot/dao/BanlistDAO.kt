package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.BannedUsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.telegram.telegrambots.meta.api.objects.User
import java.time.Clock
import java.time.Instant

class BanlistDAO {

    suspend fun banUser(user: User, reason: String?) = dbQuery {
        BannedUsersTable.insertIgnore {
            it[userId] = user.id
            it[username] = user.userName
            it[firstName] = user.firstName
            it[this.reason] = reason
            it[datetime] = Instant.now(Clock.systemUTC())
        }
            .insertedCount > 0
    }

    suspend fun unbanUser(userId: Long) = dbQuery {
        BannedUsersTable.deleteWhere { this.userId eq userId } > 0
    }

    suspend fun isUserBanned(userId: Long) = dbQuery {
        BannedUsersTable.selectAll().where { BannedUsersTable.userId eq userId }.count() > 0
    }

    suspend fun getReason(userId: Long) = dbQuery {
        BannedUsersTable.selectAll().where { BannedUsersTable.userId eq userId }.single()[BannedUsersTable.reason]
    }

    suspend fun getList() = dbQuery {
        BannedUsersTable.selectAll().toList()
    }
}