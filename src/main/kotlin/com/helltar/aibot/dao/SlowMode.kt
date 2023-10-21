package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.SlowModeTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.telegram.telegrambots.meta.api.objects.User

class SlowMode {

    fun add(user: User, limit: Int) = dbQuery {
        SlowModeTable.insertIgnore {
            it[userId] = user.id
            it[username] = user.userName
            it[firstName] = user.firstName
            it[this.limit] = limit
            it[requests] = 0
            it[lastRequestTimestamp] = 0
        }
            .insertedCount > 0
    }

    fun update(user: User, limit: Int, requests: Int = -1) = dbQuery {
        SlowModeTable.update({ SlowModeTable.userId eq user.id }) {
            it[username] = user.userName
            it[firstName] = user.firstName
            it[this.limit] = limit
            it[lastRequestTimestamp] = System.currentTimeMillis()

            if (requests > -1)
                it[this.requests] = requests
        }
    }

    fun update(userId: Long, limit: Int) = dbQuery {
        SlowModeTable.update({ SlowModeTable.userId eq userId }) {
            it[this.limit] = limit
        } > 0
    }

    fun getSlowModeState(userId: Long) = dbQuery {
        SlowModeTable.select { SlowModeTable.userId eq userId }.singleOrNull()
    }

    fun offSlowMode(userId: Long) = dbQuery {
        SlowModeTable.deleteWhere { SlowModeTable.userId eq userId } > 0
    }

    fun getList() = dbQuery {
        SlowModeTable.selectAll().toList()
    }
}