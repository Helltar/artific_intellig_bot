package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.ApiKeysTable
import com.helltar.aibot.dao.tables.ApiKeysTable.apiKey
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Clock
import java.time.Instant

class ApiKeyDAO {

    suspend fun add(provider: String, apiKey: String) = dbQuery {
        ApiKeysTable.insertIgnore {
            it[this.provider] = provider
            it[this.apiKey] = apiKey
            it[added_time] = Instant.now(Clock.systemUTC())
        }
            .insertedCount > 0
    }

    suspend fun update(provider: String, apiKey: String) = dbQuery {
        ApiKeysTable.update({ ApiKeysTable.provider eq provider }) {
            it[this.apiKey] = apiKey
            it[added_time] = Instant.now(Clock.systemUTC())
        } > 0
    }

    suspend fun getKey(provider: String) = dbQuery {
        ApiKeysTable.selectAll().where { ApiKeysTable.provider eq provider }.singleOrNull()?.get(apiKey)
    }
}