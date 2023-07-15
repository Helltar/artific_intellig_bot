package com.helltar.artific_intellig_bot

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.FileReader
import java.util.*

private const val DIR_CONFIG = "config"

const val DIR_DB = "db"
const val DIR_LOCALE = "locale"

const val FILE_BOT_CONFIG = "$DIR_CONFIG/bot_config"
const val FILE_DATABASE = "$DIR_DB/database.db"
const val EXT_DISABLED = ".disabled"

data class BotMainConfig(
    @JsonProperty(required = true)
    val token: String,

    @JsonProperty(required = true)
    val username: String,

    @JsonProperty(required = true)
    val creatorId: Long,

    @JsonProperty(required = true)
    val fileIdGifChatLoading: String
)

open class BotConfig {

    val googleCloudKey: String
    val openaiKey: String
    val stableDiffusionKey: String

    init {
        val filename = "$DIR_CONFIG/api_keys.ini"

        try {
            Properties().run {
                load(FileReader(filename))
                googleCloudKey = getProperty("google_cloud_key")
                openaiKey = getProperty("openai_key")
                stableDiffusionKey = getProperty("stable_diffusion_key")
            }
        } catch (e: Exception) {
            throw Exception("Error when reading API Keys: $filename ${e.message}")
        }
    }
}