package com.helltar.aibot

import io.github.cdimascio.dotenv.dotenv

object EnvConfig {

    val creatorId = readEnv("CREATOR_ID").toLongOrNull() ?: throw IllegalArgumentException("bad CREATOR_ID env.")

    val telegramBotToken = readEnv("BOT_TOKEN")
    val telegramBotUsername = readEnv("BOT_USERNAME")

    val postgresqlHost = readEnv("POSTGRESQL_HOST")
    val postgresqlPort = readEnv("POSTGRESQL_PORT")
    val databaseName = readEnv("DATABASE_NAME")
    val databaseUser = readEnv("DATABASE_USER")
    val databasePassword = readEnv("DATABASE_PASSWORD")

    private fun readEnv(env: String) =
        dotenv { ignoreIfMissing = true }[env].ifBlank { throw IllegalArgumentException("$env env. is blank") }
            ?: throw IllegalArgumentException("error when read $env env.")
}