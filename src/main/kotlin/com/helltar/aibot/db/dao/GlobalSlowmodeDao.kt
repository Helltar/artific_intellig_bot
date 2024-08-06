package com.helltar.aibot.db.dao

import com.helltar.aibot.db.DatabaseFactory.dbQuery
import com.helltar.aibot.db.models.GlobalSlowmodeData
import com.helltar.aibot.db.tables.GlobalSlowmodeTable
import com.helltar.aibot.db.tables.GlobalSlowmodeTable.lastUsage
import com.helltar.aibot.db.tables.GlobalSlowmodeTable.usageCount
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
            .map { GlobalSlowmodeData(it[usageCount], it[lastUsage]) }
            .singleOrNull()
    }
}

val globalSlowmodeDao = GlobalSlowmodeDao()