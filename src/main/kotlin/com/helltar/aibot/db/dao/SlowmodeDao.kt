package com.helltar.aibot.db.dao

import com.helltar.aibot.db.DatabaseFactory.dbQuery
import com.helltar.aibot.db.models.SlowmodeData
import com.helltar.aibot.db.models.SlowmodeStateData
import com.helltar.aibot.db.tables.SlowmodeTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.telegram.telegrambots.meta.api.objects.User
import java.time.Clock
import java.time.Instant

class SlowmodeDao {

    suspend fun add(user: User, limit: Int) = dbQuery {
        SlowmodeTable
            .insertIgnore {
                it[userId] = user.id
                it[username] = user.userName
                it[firstName] = user.firstName
                it[this.limit] = limit
                it[requests] = 0
                it[lastRequest] = null
            }.insertedCount > 0
    }

    suspend fun update(user: User, limit: Int, requestsCount: Int? = null) = dbQuery {
        SlowmodeTable
            .update({ SlowmodeTable.userId eq user.id }) {
                it[username] = user.userName
                it[firstName] = user.firstName
                it[this.limit] = limit
                it[lastRequest] = Instant.now(Clock.systemUTC())
                requestsCount?.let { rc -> it[requests] = rc }
            }
    }

    suspend fun update(userId: Long, limit: Int) = dbQuery {
        SlowmodeTable.update({ SlowmodeTable.userId eq userId }) { it[this.limit] = limit } > 0
    }

    suspend fun offSlowMode(userId: Long) = dbQuery {
        SlowmodeTable.deleteWhere { SlowmodeTable.userId eq userId } > 0
    }

    suspend fun getSlowmodeState(userId: Long) = dbQuery {
        SlowmodeTable
            .select(SlowmodeTable.limit, SlowmodeTable.requests, SlowmodeTable.lastRequest)
            .where { SlowmodeTable.userId eq userId }
            .map {
                SlowmodeStateData(
                    it[SlowmodeTable.limit],
                    it[SlowmodeTable.requests],
                    it[SlowmodeTable.lastRequest]
                )
            }
            .singleOrNull()
    }

    suspend fun getList() = dbQuery {
        SlowmodeTable
            .selectAll()
            .map {
                SlowmodeData(
                    it[SlowmodeTable.userId],
                    it[SlowmodeTable.username],
                    it[SlowmodeTable.firstName],
                    it[SlowmodeTable.limit],
                    it[SlowmodeTable.requests],
                    it[SlowmodeTable.lastRequest]
                )
            }
    }
}

val slowmodeDao = SlowmodeDao()