package com.helltar.aibot.config

import io.github.cdimascio.dotenv.dotenv

object Config {

    const val API_KEY_PROVIDER_OPENAI = "openai.com"
    const val API_KEY_PROVIDER_DEEPSEEK = "deepseek.com"

    val creatorId = readEnvVar("CREATOR_ID").toLongOrNull() ?: throw IllegalArgumentException("Invalid value for creator-id")
    val telegramBotToken = readEnvVar("BOT_TOKEN")
    val telegramBotUsername = readEnvVar("BOT_USERNAME")
    val postgresqlHost = readEnvVar("POSTGRESQL_HOST")
    val postgresqlPort = readEnvVar("POSTGRESQL_PORT")
    val databaseName = readEnvVar("DATABASE_NAME")
    val databaseUser = readEnvVar("DATABASE_USER")
    val databasePassword = readEnvVar("DATABASE_PASSWORD")

    private fun readEnvVar(env: String) =
        dotenv { ignoreIfMissing = true }[env].ifBlank { throw IllegalArgumentException("$env environment variable is blank") }
            ?: throw IllegalArgumentException("Error reading $env environment variable")
}
