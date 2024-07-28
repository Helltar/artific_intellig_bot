package com.helltar.aibot.dao

import com.helltar.aibot.dao.DatabaseFactory.dbQuery
import com.helltar.aibot.dao.tables.ConfigurationsTable
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class ConfigurationsDAO {

    suspend fun getGlobalSlowmodeMaxUsageCount() = dbQuery {
        getConfiguration("global_slowmode_max_usage_count")?.toIntOrNull() ?: 10
    }

    suspend fun setGlobalSlowmodeMaxUsageCount(newMax: Int) = dbQuery {
        setConfiguration("global_slowmode_max_usage_count", newMax.toString())
    }

    private suspend fun getConfiguration(key: String) = dbQuery {
        ConfigurationsTable.selectAll().where { ConfigurationsTable.key eq key }.singleOrNull()?.get(ConfigurationsTable.value)
    }

    private suspend fun setConfiguration(key: String, value: String) = dbQuery {
        ConfigurationsTable.insertIgnore {
            it[this.key] = key
            it[this.value] = value
        }

        ConfigurationsTable.update({ ConfigurationsTable.key eq key }) {
            it[this.value] = value
        }
    }
}