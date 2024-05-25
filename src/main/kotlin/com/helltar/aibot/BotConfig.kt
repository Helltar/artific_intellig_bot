package com.helltar.aibot

import io.github.cdimascio.dotenv.dotenv

object BotConfig {

    val telegramBotToken = readEnv("BOT_TOKEN").ifBlank { throw RuntimeException("bad BOT_TOKEN env") }
    val telegramBotUsername = readEnv("BOT_USERNAME").ifBlank { throw RuntimeException("bad BOT_USERNAME env") }
    val creatorId = readEnv("CREATOR_ID").toLongOrNull() ?: throw RuntimeException("bad CREATOR_ID env")


    const val DIR_LOCALE = "data/locale"
    const val DIR_FILES = "data/files"

    const val FILE_NAME_LOADING_GIF = "loading.gif"

    const val PROVIDER_OPENAI_COM = "openai.com"
    const val PROVIDER_STABILITY_AI = "stability.ai"

    val apiKeysProviders =
        setOf(
            PROVIDER_OPENAI_COM,
            PROVIDER_STABILITY_AI
        )

    private fun readEnv(env: String) =
        dotenv { ignoreIfMissing = true }[env] ?: throw RuntimeException("error when read $env env")
}