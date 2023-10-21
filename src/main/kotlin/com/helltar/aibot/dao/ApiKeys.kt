package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.ApiKeysTable
import com.helltar.aibot.dao.tables.ApiKeysTable.apiKey
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class ApiKeys {

    fun add(provider: String, apiKey: String?) = dbQuery {
        ApiKeysTable.insertIgnore {
            it[this.provider] = provider
            it[this.apiKey] = apiKey
            it[datetime] = LocalDateTime.now()
        }
            .insertedCount > 0
    }

    fun update(provider: String, apiKey: String) = dbQuery {
        ApiKeysTable.update({ ApiKeysTable.provider eq provider }) {
            it[this.apiKey] = apiKey
            it[datetime] = LocalDateTime.now()
        } > 0
    }

    fun getApiKey(provider: String) = dbQuery {
        ApiKeysTable.select { ApiKeysTable.provider eq provider }.singleOrNull()?.get(apiKey)
    }
}