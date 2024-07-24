package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.SlowmodeTable
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.telegram.telegrambots.meta.api.objects.User
import java.time.Clock
import java.time.Instant

class SlowmodeDAO {

    suspend fun add(user: User, limit: Int) = dbQuery {
        SlowmodeTable.insertIgnore {
            it[userId] = user.id
            it[username] = user.userName
            it[firstName] = user.firstName
            it[this.limit] = limit
            it[requests] = 0
            it[lastRequest] = null
        }.insertedCount > 0
    }

    suspend fun update(user: User, limit: Int, requests: Int? = null) = dbQuery {
        SlowmodeTable.update({ SlowmodeTable.userId eq user.id }) {
            it[username] = user.userName
            it[firstName] = user.firstName
            it[this.limit] = limit
            it[lastRequest] = Instant.now(Clock.systemUTC())
            requests?.let { r -> it[this.requests] = r }
        }
    }

    suspend fun update(userId: Long, limit: Int) = dbQuery {
        SlowmodeTable.update({ SlowmodeTable.userId eq userId }) { it[this.limit] = limit } > 0
    }

    suspend fun getSlowmodeState(userId: Long) = dbQuery {
        SlowmodeTable.selectAll().where { SlowmodeTable.userId eq userId }.singleOrNull()
    }

    suspend fun getList() = dbQuery {
        SlowmodeTable.selectAll().toList()
    }
}