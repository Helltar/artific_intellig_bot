package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.models.SlowmodeStatusData
import com.helltar.aibot.database.tables.SlowmodeTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.update

class SlowmodeDao {

    suspend fun add(userId: Long) = dbTransaction {
        SlowmodeTable
            .insertIgnore {
                it[this.userId] = userId
                it[createdAt] = utcNow()
            }
            .insertedCount > 0
    }

    suspend fun incrementUsageCount(userId: Long): Boolean = dbTransaction {
        SlowmodeTable
            .update({ SlowmodeTable.userId eq userId }) {
                it[this.usageCount] = usageCount + 1
                it[updatedAt] = utcNow()
            } > 0
    }

    suspend fun resetUsageCount(userId: Long): Boolean = dbTransaction {
        SlowmodeTable
            .update({ SlowmodeTable.userId eq userId }) {
                it[this.usageCount] = 1
                it[updatedAt] = utcNow()
            } > 0
    }

    suspend fun slowmodeStatus(userId: Long) = dbTransaction {
        SlowmodeTable
            .select(SlowmodeTable.usageCount, SlowmodeTable.updatedAt)
            .where { SlowmodeTable.userId eq userId }
            .singleOrNull()
            ?.let {
                SlowmodeStatusData(
                    it[SlowmodeTable.usageCount],
                    it[SlowmodeTable.updatedAt]
                )
            }
    }
}

val slowmodeDao = SlowmodeDao()
