package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbQuery
import com.helltar.aibot.database.tables.ApiKeysTable
import com.helltar.aibot.database.tables.ApiKeysTable.apiKey
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.update
import java.time.Clock
import java.time.Instant

class ApiKeyDao {

    suspend fun add(provider: String, apiKey: String) = dbQuery {
        ApiKeysTable
            .insertIgnore {
                it[this.provider] = provider
                it[this.apiKey] = apiKey
                it[added_time] = Instant.now(Clock.systemUTC())
            }
            .insertedCount > 0
    }

    suspend fun update(provider: String, apiKey: String) = dbQuery {
        ApiKeysTable
            .update({ ApiKeysTable.provider eq provider }) {
                it[this.apiKey] = apiKey
                it[added_time] = Instant.now(Clock.systemUTC())
            } > 0
    }

    suspend fun getKey(provider: String) = dbQuery {
        ApiKeysTable
            .select(apiKey)
            .where { ApiKeysTable.provider eq provider }
            .singleOrNull()?.get(apiKey)
    }
}

val apiKeyDao = ApiKeyDao()
