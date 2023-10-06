package com.helltar.artific_intellig_bot.dao

import com.helltar.artific_intellig_bot.dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import com.helltar.artific_intellig_bot.dao.tables.FilesIds as FilesIdsTable

class FilesIds {

    fun add(name: String, fileId: String) = dbQuery {
        FilesIdsTable.insertIgnore {
            it[this.name] = name
            it[this.fileId] = fileId
        }
            .insertedCount > 0
    }

    fun getFileId(name: String) = dbQuery {
        FilesIdsTable.select { FilesIdsTable.name eq name }.singleOrNull()?.get(FilesIdsTable.fileId) ?: ""
    }

    fun exists(name: String) = dbQuery {
        FilesIdsTable.select { FilesIdsTable.name eq name }.count() > 0
    }
}