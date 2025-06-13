package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.models.SudoersData
import com.helltar.aibot.database.tables.SudoersTable
import com.helltar.aibot.utils.DateTimeUtils.utcNow
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll

class SudoersDao {

    suspend fun add(userId: Long, username: String?): Boolean = dbTransaction {
        SudoersTable
            .insertIgnore {
                it[this.userId] = userId
                it[this.username] = username
                it[createdAt] = utcNow()
            }
    }
        .insertedCount > 0

    suspend fun isAdmin(userId: Long): Boolean = dbTransaction {
        SudoersTable
            .select(SudoersTable.userId)
            .where { SudoersTable.userId eq userId }
            .empty()
            .not()
    }

    suspend fun remove(userId: Long): Boolean = dbTransaction {
        SudoersTable
            .deleteWhere { this.userId eq userId } > 0
    }

    suspend fun list(): List<SudoersData> = dbTransaction {
        SudoersTable
            .selectAll()
            .map {
                SudoersData(
                    it[SudoersTable.userId],
                    it[SudoersTable.username],
                    it[SudoersTable.createdAt]
                )
            }
    }
}

val sudoersDao = SudoersDao()
