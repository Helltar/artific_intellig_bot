package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.GlobalSlowmodeTable
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Clock
import java.time.Instant

class GlobalSlowmodeDAO {

    suspend fun add(userId: Long) = dbQuery {
        GlobalSlowmodeTable.insertIgnore { it[this.userId] = userId }
    }

    suspend fun update(userId: Long, usageCount: Int) = dbQuery {
        GlobalSlowmodeTable.update({ GlobalSlowmodeTable.userId eq userId }) {
            it[this.usageCount] = usageCount
            it[lastUsage] = Instant.now(Clock.systemUTC())
        }
    }

    suspend fun getUsageState(userId: Long) = dbQuery {
        GlobalSlowmodeTable.selectAll().where { GlobalSlowmodeTable.userId eq userId }.singleOrNull()
    }
}