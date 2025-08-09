package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.tables.ApiKeysTable
import com.helltar.aibot.utils.DateTimeUtils.utcNow
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import java.util.concurrent.ConcurrentHashMap

class ApiKeyDao {

    private val cache = ConcurrentHashMap<String, String>()

    suspend fun add(provider: String, apiKey: String): Boolean = dbTransaction {
        (ApiKeysTable
            .insertIgnore {
                it[this.provider] = provider
                it[this.apiKey] = apiKey
                it[createdAt] = utcNow()
            }
            .insertedCount > 0).also { if (it) cache[provider] = apiKey }
    }

    suspend fun update(provider: String, apiKey: String): Boolean = dbTransaction {
        (ApiKeysTable
            .update({ ApiKeysTable.provider eq provider }) {
                it[this.apiKey] = apiKey
                it[updatedAt] = utcNow()
            } > 0).also { if (it) cache[provider] = apiKey }
    }

    suspend fun getKey(provider: String): String? =
        cache[provider]
            ?: dbTransaction {
                ApiKeysTable
                    .select(ApiKeysTable.apiKey)
                    .where { ApiKeysTable.provider eq provider }
                    .singleOrNull()?.getOrNull(ApiKeysTable.apiKey)
            }
                ?.also { cache[provider] = it }
}

val apiKeyDao = ApiKeyDao()
