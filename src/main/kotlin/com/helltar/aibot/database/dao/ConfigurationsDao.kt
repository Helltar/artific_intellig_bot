package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.tables.ConfigurationsTable
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.update

class ConfigurationsDao {

    suspend fun getGlobalSlowmodeMaxUsageCount() = dbTransaction {
        getConfigValue("global_slowmode_max_usage_count")?.toIntOrNull() ?: 10
    }

    suspend fun setGlobalSlowmodeMaxUsageCount(newMax: Int) = dbTransaction {
        setConfiguration("global_slowmode_max_usage_count", newMax.toString())
    }

    suspend fun isDeepSeekEnabled() = dbTransaction {
        getConfigValue("deepseek_enabled")?.toBoolean() == true
    }

    suspend fun setDeepSeekState(enable: Boolean) = dbTransaction {
        setConfiguration("deepseek_enabled", enable.toString())
    }

    private fun getConfigValue(key: String): String? =
        ConfigurationsTable
            .select(ConfigurationsTable.value)
            .where { ConfigurationsTable.key eq key }
            .singleOrNull()?.get(ConfigurationsTable.value)

    private fun setConfiguration(key: String, value: String): Boolean =
        (ConfigurationsTable
            .update({ ConfigurationsTable.key eq key }) {
                it[this.value] = value
                it[this.updatedAt] = utcNow()
            }.takeIf { it > 0 }
            ?: ConfigurationsTable
                .insertIgnore {
                    it[this.key] = key
                    it[this.value] = value
                    it[this.createdAt] = utcNow()
                }.insertedCount) > 0
}

val configurationsDao = ConfigurationsDao()
