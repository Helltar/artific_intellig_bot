package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import com.helltar.aibot.dao.tables.FilesTable as FilesIdsTable

class FilesDAO {

    suspend fun add(name: String, fileId: String) = dbQuery {
        FilesIdsTable.insertIgnore {
            it[this.name] = name
            it[this.fileId] = fileId
        }
            .insertedCount > 0
    }

    suspend fun getFileId(name: String) = dbQuery {
        FilesIdsTable.selectAll().where { FilesIdsTable.name eq name }.singleOrNull()?.get(FilesIdsTable.fileId)
    }

    suspend fun delete(name: String) = dbQuery {
        FilesIdsTable.deleteWhere { FilesIdsTable.name eq name }
    }
}