package com.helltar.aibot.database.dao

import com.helltar.aibot.database.Database.dbTransaction
import com.helltar.aibot.database.Database.utcNow
import com.helltar.aibot.database.tables.ConfigurationsTable
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.update

class ConfigurationsDao {

    private companion object {
        const val KEY_SLOWMODE_MAX_USAGE_COUNT = "global_slowmode_max_usage_count"
        const val KEY_DEEPSEEK_ENABLED = "deepseek_enabled"
        const val KEY_LOADING_GIF_FILE_ID = "loading_gif_file_id"
    }

    suspend fun getLoadingGifFileId() =
        getConfigValue(KEY_LOADING_GIF_FILE_ID)

    suspend fun updateLoadingGifFileId(fileId: String) =
        setConfiguration(KEY_LOADING_GIF_FILE_ID, fileId)

    suspend fun getSlowmodeMaxUsageCount() =
        getConfigValue(KEY_SLOWMODE_MAX_USAGE_COUNT)?.toIntOrNull() ?: 10

    suspend fun updateSlowmodeMaxUsageCount(newMax: Int) =
        setConfiguration(KEY_SLOWMODE_MAX_USAGE_COUNT, newMax.toString())

    suspend fun isDeepSeekEnabled() =
        getConfigValue(KEY_DEEPSEEK_ENABLED)?.toBoolean() == true

    suspend fun updateDeepSeekState(enable: Boolean) =
        setConfiguration(KEY_DEEPSEEK_ENABLED, enable.toString())

    private suspend fun getConfigValue(key: String): String? = dbTransaction {
        ConfigurationsTable
            .select(ConfigurationsTable.value)
            .where { ConfigurationsTable.key eq key }
            .singleOrNull()?.get(ConfigurationsTable.value)
    }

    private suspend fun setConfiguration(key: String, value: String): Boolean = dbTransaction {
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
}

val configurationsDao = ConfigurationsDao()
