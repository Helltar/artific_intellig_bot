package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.models.GlobalSlowmodeData
import com.helltar.aibot.database.tables.GlobalSlowmodeTable
import com.helltar.aibot.database.tables.GlobalSlowmodeTable.usageCount
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.update

class GlobalSlowmodeDao {

    suspend fun add(userId: Long) = dbTransaction {
        GlobalSlowmodeTable
            .insertIgnore {
                it[this.userId] = userId
                it[this.createdAt] = utcNow()
            }
    }

    suspend fun incrementUsageCount(userId: Long) = dbTransaction {
        GlobalSlowmodeTable
            .update({ GlobalSlowmodeTable.userId eq userId }) {
                it[usageCount] = usageCount + 1
                it[updatedAt] = utcNow()
            }
    }

    suspend fun resetUsageCount(userId: Long): Boolean = dbTransaction {
        GlobalSlowmodeTable
            .update({ GlobalSlowmodeTable.userId eq userId }) {
                it[this.usageCount] = 0
                it[updatedAt] = utcNow()
            } > 0
    }

    suspend fun userSlowmodeData(userId: Long): GlobalSlowmodeData? = dbTransaction {
        GlobalSlowmodeTable
            .select(usageCount, GlobalSlowmodeTable.updatedAt)
            .where { GlobalSlowmodeTable.userId eq userId }
            .singleOrNull()
            ?.let { GlobalSlowmodeData(it[usageCount], it[GlobalSlowmodeTable.updatedAt]) }
    }
}

val globalSlowmodeDao = GlobalSlowmodeDao()
