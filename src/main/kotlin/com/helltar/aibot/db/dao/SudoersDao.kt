package com.helltar.aibot.db.dao

import com.helltar.aibot.db.DatabaseFactory.dbQuery
import com.helltar.aibot.db.models.SudoersData
import com.helltar.aibot.db.tables.SudoersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import java.time.Clock
import java.time.Instant

class SudoersDao {

    suspend fun add(userId: Long, username: String?) = dbQuery {
        SudoersTable.insertIgnore {
            it[this.userId] = userId
            it[this.username] = username
            it[datetime] = Instant.now(Clock.systemUTC())
        }
    }
        .insertedCount > 0


    suspend fun isAdmin(userId: Long) = dbQuery {
        SudoersTable
            .select(SudoersTable.userId)
            .where { SudoersTable.userId eq userId }
            .count() > 0
    }

    suspend fun remove(userId: Long) = dbQuery {
        SudoersTable.deleteWhere { this.userId eq userId } > 0
    }

    suspend fun getList() = dbQuery {
        SudoersTable
            .selectAll()
            .map {
                SudoersData(
                    it[SudoersTable.userId],
                    it[SudoersTable.username],
                    it[SudoersTable.datetime]
                )
            }
    }
}

val sudoersDao = SudoersDao()