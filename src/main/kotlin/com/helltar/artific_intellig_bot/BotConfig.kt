package com.helltar.artific_intellig_bot

import com.helltar.artific_intellig_bot.Utils.getLineFromFile
import com.helltar.artific_intellig_bot.Utils.getListFromFile
import com.helltar.artific_intellig_bot.Utils.getTextFromFile
import java.io.FileReader
import java.util.*

private const val DIR_CONFIG = "config"
private const val DIR_JSON = "$DIR_CONFIG/json"

const val DIR_DB = "db/"
const val DIR_STABLE_DIFFUSION = "stable_diffusion/"
const val DIR_TEXT_TO_SPEECH = "text_to_speech/"
const val EXT_DISABLED = ".disabled"

val BOT_USERNAME = getLineFromFile("$DIR_CONFIG/bot_username.txt")
val BOT_TOKEN = getLineFromFile("$DIR_CONFIG/bot_token.txt")

open class BotConfig {

    val googleCloudKey: String
    val openaiKey: String
    val stableDiffusionKey: String

    fun getChatsWhiteList() = getListFromFile("$DIR_CONFIG/chats_white_list.txt")
    fun getSudoers() = getListFromFile("$DIR_CONFIG/sudoers.txt")

    fun getJsonChatGPT() = getTextFromFile("$DIR_JSON/ChatGPT.json")
    fun getJsonDalle2() = getTextFromFile("$DIR_JSON/DallE2.json")
    fun getJsonStableDiffusion() = getTextFromFile("$DIR_JSON/StableDiffusion.json")
    fun getJsonTextToSpeech() = getTextFromFile("$DIR_JSON/TextToSpeech.json")

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
