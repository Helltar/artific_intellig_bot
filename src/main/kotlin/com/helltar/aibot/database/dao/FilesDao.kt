package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.tables.FilesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.update

class FilesDao {

    suspend fun add(fileName: String, fileId: String): Boolean = dbTransaction {
        FilesTable
            .insertIgnore {
                it[this.fileName] = fileName
                it[this.fileId] = fileId
                it[this.createdAt] = utcNow()
            }
            .insertedCount > 0
    }

    suspend fun getFileId(fileName: String): String? = dbTransaction {
        FilesTable
            .select(FilesTable.fileId)
            .where { FilesTable.fileName eq fileName }
            .singleOrNull()?.get(FilesTable.fileId)
    }

    suspend fun update(fileName: String, fileId: String): Boolean = dbTransaction {
        FilesTable
            .update({ FilesTable.fileName eq fileName }) {
                it[this.fileId] = fileId
                it[this.updatedAt] = utcNow()
            } > 0
    }

    suspend fun delete(fileName: String) = dbTransaction {
        FilesTable
            .deleteWhere { FilesTable.fileName eq fileName }
    }
}

val filesDao = FilesDao()
