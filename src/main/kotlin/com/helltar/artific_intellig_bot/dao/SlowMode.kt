package com.helltar.artific_intellig_bot.dao

import com.helltar.artific_intellig_bot.dao.DatabaseFactory.dbQuery
import com.helltar.artific_intellig_bot.dao.tables.SlowMode.lastRequestTimestamp
import com.helltar.artific_intellig_bot.dao.tables.SlowMode.limit
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.telegram.telegrambots.meta.api.objects.User
import com.helltar.artific_intellig_bot.dao.tables.SlowMode as SlowModeTable

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

    fun getRequestsSize(userId: Long) = dbQuery {
        SlowModeTable.select { SlowModeTable.userId eq userId }.singleOrNull()?.get(SlowModeTable.requests) ?: -1
    }

    fun getLimitSize(userId: Long) = dbQuery {
        SlowModeTable.select { SlowModeTable.userId eq userId }.single()[limit]
    }

    fun getLastRequestTimestamp(userId: Long) = dbQuery {
        SlowModeTable.select { SlowModeTable.userId eq userId }.single()[lastRequestTimestamp]
    }

    fun off(userId: Long) = dbQuery {
        SlowModeTable.deleteWhere { SlowModeTable.userId eq userId } > 0
    }

    fun getList() = dbQuery {
        SlowModeTable.selectAll().toList()
    }
}