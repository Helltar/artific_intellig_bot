package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.ApiKeyType
import com.helltar.aibot.dao.tables.ApiKeysTable
import com.helltar.aibot.dao.tables.ApiKeysTable.apiKey
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class ApiKeyDAO {

    fun add(provider: String, apiKey: String, type: ApiKeyType) = dbQuery {
        ApiKeysTable.insertIgnore {
            it[this.provider] = provider
            it[this.apiKey] = apiKey
            it[this.type] = type
            it[datetime] = LocalDateTime.now()
        }
            .insertedCount > 0
    }

    fun update(provider: String, apiKey: String, type: ApiKeyType) = dbQuery {
        ApiKeysTable.update({ ApiKeysTable.provider eq provider and (ApiKeysTable.type eq type) }) {
            it[this.apiKey] = apiKey
            it[this.type] = type
            it[datetime] = LocalDateTime.now()
        } > 0
    }

    fun getApiKey(provider: String, type: ApiKeyType) = dbQuery {
        ApiKeysTable.select { ApiKeysTable.provider eq provider and (ApiKeysTable.type eq type) }.singleOrNull()?.get(apiKey)
    }
}