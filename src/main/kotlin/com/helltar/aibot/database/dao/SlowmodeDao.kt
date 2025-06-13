package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.models.SlowmodeStatusData
import com.helltar.aibot.database.tables.SlowmodeTable
import com.helltar.aibot.utils.DateTimeUtils.utcNow
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.plus
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update

class SlowmodeDao {

    suspend fun registerUser(userId: Long): Boolean = dbTransaction {
        SlowmodeTable
            .insertIgnore {
                it[this.userId] = userId
                it[updatedAt] = utcNow()
                it[createdAt] = utcNow()
            }
            .insertedCount > 0
    }

    suspend fun incrementUsageCount(userId: Long): Boolean = dbTransaction {
        SlowmodeTable
            .update({ SlowmodeTable.userId eq userId }) {
                it[usageCount] = usageCount + 1
                it[updatedAt] = utcNow()
            } > 0
    }

    suspend fun resetUsageCount(userId: Long): Boolean = dbTransaction {
        SlowmodeTable
            .update({ SlowmodeTable.userId eq userId }) {
                it[usageCount] = 1
                it[updatedAt] = utcNow()
            } > 0
    }

    suspend fun slowmodeStatus(userId: Long): SlowmodeStatusData? = dbTransaction {
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
