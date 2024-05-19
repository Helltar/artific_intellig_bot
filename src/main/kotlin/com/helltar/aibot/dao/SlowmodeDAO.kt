package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.SlowmodeTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.telegram.telegrambots.meta.api.objects.User

class SlowmodeDAO {

    fun add(user: User, limit: Int) = dbQuery {
        SlowmodeTable.insertIgnore {
            it[userId] = user.id
            it[username] = user.userName
            it[firstName] = user.firstName
            it[this.limit] = limit
            it[requests] = 0
            it[lastRequestTimestamp] = 0
        }
            .insertedCount > 0
    }

    fun add(userId: Long, limit: Int) = dbQuery {
        SlowmodeTable.insertIgnore {
            it[this.userId] = userId
            it[username] = null
            it[firstName] = "null"
            it[this.limit] = limit
            it[requests] = 0
            it[lastRequestTimestamp] = 0
        }
            .insertedCount > 0
    }

    fun update(user: User, limit: Int, requests: Int = -1) = dbQuery {
        SlowmodeTable.update({ SlowmodeTable.userId eq user.id }) {
            it[username] = user.userName
            it[firstName] = user.firstName
            it[this.limit] = limit
            it[lastRequestTimestamp] = System.currentTimeMillis()

            if (requests > -1)
                it[this.requests] = requests
        }
    }

    fun update(userId: Long, limit: Int) = dbQuery {
        SlowmodeTable.update({ SlowmodeTable.userId eq userId }) {
            it[this.limit] = limit
        } > 0
    }

    fun getSlowModeState(userId: Long) = dbQuery {
        SlowmodeTable.select { SlowmodeTable.userId eq userId }.singleOrNull()
    }

    fun offSlowMode(userId: Long) = dbQuery {
        SlowmodeTable.deleteWhere { SlowmodeTable.userId eq userId } > 0
    }

    fun getList() = dbQuery {
        SlowmodeTable.selectAll().toList()
    }
}