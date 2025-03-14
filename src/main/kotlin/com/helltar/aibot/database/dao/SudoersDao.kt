package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.models.SudoersData
import com.helltar.aibot.database.tables.SudoersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll

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
