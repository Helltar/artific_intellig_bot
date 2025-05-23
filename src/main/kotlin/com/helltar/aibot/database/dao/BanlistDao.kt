package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.models.BanlistData
import com.helltar.aibot.database.tables.BannedUsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.telegram.telegrambots.meta.api.objects.User

class BanlistDao {

    suspend fun ban(user: User, reason: String?): Boolean = dbTransaction {
        BannedUsersTable
            .insertIgnore {
                it[userId] = user.id
                it[username] = user.userName
                it[firstName] = user.firstName
                it[this.reason] = reason
                it[bannedAt] = utcNow()
            }
            .insertedCount > 0
    }

    suspend fun unban(userId: Long): Boolean = dbTransaction {
        BannedUsersTable
            .deleteWhere { BannedUsersTable.userId eq userId } > 0
    }

    suspend fun isBanned(userId: Long): Boolean = dbTransaction {
        BannedUsersTable
            .select(BannedUsersTable.userId)
            .where { BannedUsersTable.userId eq userId }
            .empty()
            .not()
    }

    suspend fun reason(userId: Long): String? = dbTransaction {
        BannedUsersTable
            .select(BannedUsersTable.reason)
            .where { BannedUsersTable.userId eq userId }
            .singleOrNull()?.getOrNull(BannedUsersTable.reason)
    }

    suspend fun list(): List<BanlistData> = dbTransaction {
        BannedUsersTable
            .selectAll()
            .map {
                BanlistData(
                    it[BannedUsersTable.userId],
                    it[BannedUsersTable.username],
                    it[BannedUsersTable.firstName],
                    it[BannedUsersTable.reason],
                    it[BannedUsersTable.bannedAt]
                )
            }
    }

}

val banlistDao = BanlistDao()
