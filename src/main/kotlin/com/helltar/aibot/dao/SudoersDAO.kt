package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.SudoersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime

class SudoersDAO {

    fun add(userId: Long, username: String?) = dbQuery {
        SudoersTable.insertIgnore {
            it[this.userId] = userId
            it[this.username] = username
            it[datetime] = LocalDateTime.now()
        }
    }
        .insertedCount > 0

    fun remove(userId: Long) = dbQuery {
        SudoersTable.deleteWhere { this.userId eq userId } > 0
    }

    fun getList() = dbQuery {
        SudoersTable.selectAll().toList()
    }

    fun isAdmin(userId: Long) = dbQuery {
        SudoersTable.select { SudoersTable.userId eq userId }.count() > 0
    }
}