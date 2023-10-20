package com.helltar.aibot

import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*

object BotConfig {

    data class JsonData(
        @JsonProperty(required = true)
        val token: String,

        @JsonProperty(required = true)
        val username: String,

        @JsonProperty(required = true)
        val creatorId: Long
    )

    private const val DIR_CONFIG = "config"
    const val DIR_DB = "database"
    const val DIR_LOCALE = "locale"
    const val DIR_FILES = "files"
    const val DIR_TEMP = "tmp"

    private const val FILE_API_KEYS = "$DIR_CONFIG/api_keys.ini"
    const val FILE_BOT_CONFIG = "$DIR_CONFIG/bot_config"
    const val FILE_DATABASE = "$DIR_DB/database.db"
    const val FILE_LOADING_GIF = "loading.gif"

    fun getOpenaiApiKey(): String =
        getApiKey("openai_key")

    fun getStableDiffusionApiKey(): String =
        getApiKey("stable_diffusion_key")

    private val log = LoggerFactory.getLogger(javaClass)

    private fun getApiKey(name: String) =
        try {
            Properties().run {
                load(FileReader(FILE_API_KEYS))
                getProperty(name, "")
            }
        } catch (e: FileNotFoundException) {
            log.error(e.message)
            ""
        }
}