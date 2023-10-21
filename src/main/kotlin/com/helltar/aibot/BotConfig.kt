package com.helltar.aibot

import com.fasterxml.jackson.annotation.JsonProperty

object BotConfig {

    private const val DIR_CONFIG = "config"

    const val DIR_DB = "database"
    const val DIR_LOCALE = "locale"
    const val DIR_FILES = "files"
    const val FILE_BOT_CONFIG = "$DIR_CONFIG/bot_config"
    const val FILE_DATABASE = "$DIR_DB/database.db"
    const val FILE_LOADING_GIF = "loading.gif"

    const val PROVIDER_OPENAI_COM = "openai.com"
    const val PROVIDER_STABILITY_AI = "stability.ai"

    val availableApiProviders = setOf(PROVIDER_OPENAI_COM, PROVIDER_STABILITY_AI)

    data class JsonData(
        @JsonProperty(required = true)
        val token: String,

        @JsonProperty(required = true)
        val username: String,

        @JsonProperty(required = true)
        val creatorId: Long
    )
}