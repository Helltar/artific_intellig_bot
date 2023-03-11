package com.helltar.artific_intellig_bot.db

import com.helltar.artific_intellig_bot.db.Database.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class Sudoers {

    fun add(userId: Long, username: String = "") = dbQuery {
        SudoersTable.insertIgnore {
            it[this.userId] = userId
            it[this.username] = username
        }
    }
        .insertedCount > 0

    fun remove(userId: Long) = dbQuery {
        SudoersTable.deleteWhere { this.userId eq userId } > 0
    }

    fun getList() = dbQuery {
        var text = ""

        SudoersTable.selectAll().forEach {
            text += "<code>${it[SudoersTable.userId]}</code> <b>${it[SudoersTable.username]}</b>\n"
        }

        return@dbQuery text
    }

    fun isAdmin(userId: Long) = dbQuery {
        SudoersTable.select { SudoersTable.userId eq userId }.count() > 0
    }

    fun isCreator(userId: Long) = dbQuery {
        return@dbQuery userId == SudoersTable.selectAll().limit(1).single()[SudoersTable.userId]
    }
}
