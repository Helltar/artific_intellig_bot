package com.helltar.aibot.db.dao

import com.helltar.aibot.db.DatabaseFactory.dbQuery
import com.helltar.aibot.db.models.BanlistData
import com.helltar.aibot.db.tables.BannedUsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.telegram.telegrambots.meta.api.objects.User
import java.time.Clock
import java.time.Instant

class BanlistDao {

    suspend fun banUser(user: User, reason: String?) = dbQuery {
        BannedUsersTable
            .insertIgnore {
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
        BannedUsersTable
            .select(BannedUsersTable.userId)
            .where { BannedUsersTable.userId eq userId }
            .count() > 0
    }

    suspend fun getReason(userId: Long) = dbQuery {
        BannedUsersTable
            .select(BannedUsersTable.reason)
            .where { BannedUsersTable.userId eq userId }
            .singleOrNull()?.get(BannedUsersTable.reason)
    }

    suspend fun getList(): List<BanlistData> = dbQuery {
        BannedUsersTable
            .selectAll()
            .map {
                BanlistData(
                    it[BannedUsersTable.userId],
                    it[BannedUsersTable.username],
                    it[BannedUsersTable.firstName],
                    it[BannedUsersTable.reason],
                    it[BannedUsersTable.datetime]
                )
            }
    }

}

val banlistDao = BanlistDao()