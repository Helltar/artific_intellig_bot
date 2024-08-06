package com.helltar.aibot.db.dao

import com.helltar.aibot.db.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import com.helltar.aibot.db.tables.FilesTable as FilesIdsTable

class FilesDao {

    suspend fun add(name: String, fileId: String) = dbQuery {
        FilesIdsTable
            .insertIgnore {
                it[this.name] = name
                it[this.fileId] = fileId
            }
            .insertedCount > 0
    }

    suspend fun getFileId(name: String) = dbQuery {
        FilesIdsTable
            .select(FilesIdsTable.fileId)
            .where { FilesIdsTable.name eq name }
            .singleOrNull()?.get(FilesIdsTable.fileId)
    }

    suspend fun delete(name: String) = dbQuery {
        FilesIdsTable.deleteWhere { FilesIdsTable.name eq name }
    }
}

val filesDao = FilesDao()