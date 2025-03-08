package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.models.SlowmodeData
import com.helltar.aibot.database.models.SlowmodeStateData
import com.helltar.aibot.database.tables.SlowmodeTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.telegram.telegrambots.meta.api.objects.User

class SlowmodeDao {

    suspend fun add(user: User, limit: Int) = dbTransaction {
        SlowmodeTable
            .insertIgnore {
                it[userId] = user.id
                it[username] = user.userName
                it[firstName] = user.firstName
                it[this.limit] = limit
                it[createdAt] = utcNow()
            }
            .insertedCount > 0
    }

    suspend fun updateUserDataAndLimit(user: User, limit: Int) = dbTransaction {
        SlowmodeTable
            .update({ SlowmodeTable.userId eq user.id }) {
                it[username] = user.userName
                it[firstName] = user.firstName
                it[this.limit] = limit
                it[updatedAt] = utcNow()
            }
    }

    suspend fun updateLimit(userId: Long, limit: Int): Boolean = dbTransaction {
        SlowmodeTable
            .update({ SlowmodeTable.userId eq userId }) {
                it[this.limit] = limit
                it[updatedAt] = utcNow()
            } > 0
    }

    suspend fun incrementUsageCount(userId: Long): Boolean = dbTransaction {
        SlowmodeTable
            .update({ SlowmodeTable.userId eq userId }) {
                it[this.usageCount] = usageCount + 1
                it[updatedAt] = utcNow()
            } > 0
    }

    suspend fun releaseUsageCount(userId: Long): Boolean = dbTransaction {
        SlowmodeTable
            .update({ SlowmodeTable.userId eq userId }) {
                it[this.usageCount] = 0
                it[updatedAt] = utcNow()
            } > 0
    }

    suspend fun disableSlowmode(userId: Long): Boolean = dbTransaction {
        SlowmodeTable
            .deleteWhere { SlowmodeTable.userId eq userId } > 0
    }

    suspend fun slowmodeState(userId: Long) = dbTransaction {
        SlowmodeTable
            .select(SlowmodeTable.limit, SlowmodeTable.usageCount, SlowmodeTable.updatedAt)
            .where { SlowmodeTable.userId eq userId }
            .singleOrNull()
            ?.let {
                SlowmodeStateData(
                    it[SlowmodeTable.limit],
                    it[SlowmodeTable.usageCount],
                    it[SlowmodeTable.updatedAt]
                )
            }
    }

    suspend fun list() = dbTransaction {
        SlowmodeTable
            .selectAll()
            .map {
                SlowmodeData(
                    it[SlowmodeTable.userId],
                    it[SlowmodeTable.username],
                    it[SlowmodeTable.firstName],
                    it[SlowmodeTable.limit],
                    it[SlowmodeTable.usageCount],
                    it[SlowmodeTable.updatedAt]
                )
            }
    }
}

val slowmodeDao = SlowmodeDao()
