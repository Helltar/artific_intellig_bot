package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbQuery
import com.helltar.aibot.database.models.GlobalSlowmodeData
import com.helltar.aibot.database.tables.GlobalSlowmodeTable
import com.helltar.aibot.database.tables.GlobalSlowmodeTable.lastUsage
import com.helltar.aibot.database.tables.GlobalSlowmodeTable.usageCount
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.update
import java.time.Clock
import java.time.Instant

class GlobalSlowmodeDao {

    suspend fun add(userId: Long) = dbQuery {
        GlobalSlowmodeTable.insertIgnore { it[this.userId] = userId }
    }

    suspend fun update(userId: Long, usageCount: Int) = dbQuery {
        GlobalSlowmodeTable.update({ GlobalSlowmodeTable.userId eq userId }) {
            it[this.usageCount] = usageCount
            it[lastUsage] = Instant.now(Clock.systemUTC())
        }
    }

    suspend fun getUsageState(userId: Long): GlobalSlowmodeData? = dbQuery {
        GlobalSlowmodeTable
            .select(usageCount, lastUsage)
            .where { GlobalSlowmodeTable.userId eq userId }
            .singleOrNull()
            ?.let { GlobalSlowmodeData(it[usageCount], it[lastUsage]) }
    }
}

val globalSlowmodeDao = GlobalSlowmodeDao()
