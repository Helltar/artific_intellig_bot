package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.SudoersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import java.time.Clock
import java.time.Instant

class SudoersDAO {

    suspend fun add(userId: Long, username: String?) = dbQuery {
        SudoersTable.insertIgnore {
            it[this.userId] = userId
            it[this.username] = username
            it[datetime] = Instant.now(Clock.systemUTC())
        }
    }
        .insertedCount > 0

    suspend fun remove(userId: Long) = dbQuery {
        SudoersTable.deleteWhere { this.userId eq userId } > 0
    }

    suspend fun getList() = dbQuery {
        SudoersTable.selectAll().toList()
    }

    suspend fun isAdmin(userId: Long) = dbQuery {
        SudoersTable.selectAll().where { SudoersTable.userId eq userId }.count() > 0
    }
}