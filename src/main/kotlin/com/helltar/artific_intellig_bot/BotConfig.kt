package com.helltar.artific_intellig_bot

import com.helltar.artific_intellig_bot.Utils.getLineFromFile
import com.helltar.artific_intellig_bot.Utils.getListFromFile
import com.helltar.artific_intellig_bot.Utils.getTextFromFile

object BotConfig {

    private const val DIR_CONFIG = "config"
    private const val DIR_JSON = "$DIR_CONFIG/json"

    const val DIR_DB = "db/"
    const val DIR_STABLE_DIFFUSION = "stable_diffusion/"
    const val EXT_DISABLED = ".disabled"

    val JSON_STABLE_DIFFUSION = getTextFromFile("$DIR_JSON/StableDiffusion.json")
    val JSON_CHATGPT = getTextFromFile("$DIR_JSON/ChatGPT.json")
    val JSON_DALLE2 = getTextFromFile("$DIR_JSON/DallE2.json")

    val SUDOERS = getListFromFile("$DIR_CONFIG/sudoers.txt")

    val BOT_USERNAME = getLineFromFile("$DIR_CONFIG/bot_username.txt")
    val BOT_TOKEN = getLineFromFile("$DIR_CONFIG/bot_token.txt")
    val STABLE_DIFFUSION_TOKEN = getLineFromFile("$DIR_CONFIG/stable_diffusion_token.txt")
    val OPENAI_TOKEN = getLineFromFile("$DIR_CONFIG/openai_token.txt")
}
