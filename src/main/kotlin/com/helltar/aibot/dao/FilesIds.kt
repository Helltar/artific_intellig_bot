package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import com.helltar.aibot.dao.tables.FilesIds as FilesIdsTable

class FilesIds {

    fun add(name: String, fileId: String) = dbQuery {
        FilesIdsTable.insertIgnore {
            it[this.name] = name
            it[this.fileId] = fileId
        }
            .insertedCount > 0
    }

    fun getFileId(name: String) = dbQuery {
        FilesIdsTable.select { FilesIdsTable.name eq name }.singleOrNull()?.get(FilesIdsTable.fileId)
    }
}