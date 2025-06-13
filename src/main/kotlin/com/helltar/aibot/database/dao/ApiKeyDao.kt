package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.tables.ApiKeysTable
import com.helltar.aibot.utils.DateTimeUtils.utcNow
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update

class ApiKeyDao {

    suspend fun add(provider: String, apiKey: String): Boolean = dbTransaction {
        ApiKeysTable
            .insertIgnore {
                it[this.provider] = provider
                it[this.apiKey] = apiKey
                it[createdAt] = utcNow()
            }
            .insertedCount > 0
    }

    suspend fun update(provider: String, apiKey: String): Boolean = dbTransaction {
        ApiKeysTable
            .update({ ApiKeysTable.provider eq provider }) {
                it[this.apiKey] = apiKey
                it[updatedAt] = utcNow()
            } > 0
    }

    suspend fun getKey(provider: String): String? = dbTransaction {
        ApiKeysTable
            .select(ApiKeysTable.apiKey)
            .where { ApiKeysTable.provider eq provider }
            .singleOrNull()?.getOrNull(ApiKeysTable.apiKey)
    }
}

val apiKeyDao = ApiKeyDao()
