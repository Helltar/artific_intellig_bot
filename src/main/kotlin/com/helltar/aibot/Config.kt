package com.helltar.aibot

import io.github.cdimascio.dotenv.dotenv

object Config {

    const val LOADING_GIF_FILE_NAME = "loading.gif"
    const val DIR_FILES = "data/files"

    const val PROVIDER_OPENAI = "openai.com"
    const val PROVIDER_DEEPSEEK = "deepseek.com"

    @Suppress("unused")
    val apiKeyProviders = setOf(PROVIDER_OPENAI, PROVIDER_DEEPSEEK)

    private const val CREATOR_ID_KEY = "CREATOR_ID"
    private const val BOT_TOKEN_KEY = "BOT_TOKEN"
    private const val BOT_USERNAME_KEY = "BOT_USERNAME"

    private const val POSTGRESQL_HOST_KEY = "POSTGRESQL_HOST"
    private const val POSTGRESQL_PORT_KEY = "POSTGRESQL_PORT"
    private const val DATABASE_NAME_KEY = "DATABASE_NAME"
    private const val DATABASE_USER_KEY = "DATABASE_USER"
    private const val DATABASE_PASSWORD_KEY = "DATABASE_PASSWORD"

    val creatorId = readEnvVar(CREATOR_ID_KEY).toLongOrNull() ?: throw IllegalArgumentException("Invalid value for $CREATOR_ID_KEY")
    val telegramBotToken = readEnvVar(BOT_TOKEN_KEY)
    val telegramBotUsername = readEnvVar(BOT_USERNAME_KEY)

    val postgresqlHost = readEnvVar(POSTGRESQL_HOST_KEY)
    val postgresqlPort = readEnvVar(POSTGRESQL_PORT_KEY)
    val databaseName = readEnvVar(DATABASE_NAME_KEY)
    val databaseUser = readEnvVar(DATABASE_USER_KEY)
    val databasePassword = readEnvVar(DATABASE_PASSWORD_KEY)

    private fun readEnvVar(env: String) =
        dotenv { ignoreIfMissing = true }[env].ifBlank { throw IllegalArgumentException("$env environment variable is blank") }
            ?: throw IllegalArgumentException("Error reading $env environment variable")
}
