package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbQuery
import com.helltar.aibot.database.tables.ConfigurationsTable
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.update

class ConfigurationsDao {

    suspend fun getGlobalSlowmodeMaxUsageCount() = dbQuery {
        getConfiguration("global_slowmode_max_usage_count")?.toIntOrNull() ?: 10
    }

    suspend fun setGlobalSlowmodeMaxUsageCount(newMax: Int) = dbQuery {
        setConfiguration("global_slowmode_max_usage_count", newMax.toString())
    }

    suspend fun isDeepSeekEnabled() = dbQuery {
        getConfiguration("deepseek_enabled")?.toBoolean() == true
    }

    suspend fun setDeepSeekState(enable: Boolean) = dbQuery {
        setConfiguration("deepseek_enabled", enable.toString())
    }

    private suspend fun getConfiguration(key: String) = dbQuery {
        ConfigurationsTable
            .select(ConfigurationsTable.value)
            .where { ConfigurationsTable.key eq key }
            .singleOrNull()?.get(ConfigurationsTable.value)
    }

    private suspend fun setConfiguration(key: String, value: String) = dbQuery { // todo: insert update
        ConfigurationsTable.insertIgnore {
            it[this.key] = key
            it[this.value] = value
        }

        ConfigurationsTable
            .update({ ConfigurationsTable.key eq key }) { it[this.value] = value }
    }
}

val configurationsDao = ConfigurationsDao()
