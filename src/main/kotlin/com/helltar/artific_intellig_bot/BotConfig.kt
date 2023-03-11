package com.helltar.artific_intellig_bot

import com.fasterxml.jackson.annotation.JsonProperty
import com.helltar.artific_intellig_bot.Utils.getTextFromFile
import java.io.FileReader
import java.util.*

private const val DIR_CONFIG = "config"
private const val DIR_JSON = "$DIR_CONFIG/json"

const val DIR_DB = "db/"
const val DIR_OUT_DALLE2_VARIATIONS = "dalle2_variations/"
const val DIR_OUT_STABLE_DIFFUSION = "stable_diffusion/"
const val DIR_OUT_TEXT_TO_SPEECH = "text_to_speech/"
const val EXT_DISABLED = ".disabled"
const val FILE_BOT_CONFIG = "$DIR_CONFIG/bot_config"
const val FILE_DATABASE = DIR_DB + "database.db"

data class BotMainConfig(
    @JsonProperty(required = true)
    val token: String,

    @JsonProperty(required = true)
    val username: String,

    @JsonProperty(required = true)
    val creatorId: Long
)

open class BotConfig {

    val googleCloudKey: String
    val openaiKey: String
    val stableDiffusionKey: String

    fun getJsonChatGPT() =
        getTextFromFile("$DIR_JSON/ChatGPT.json")

    fun getJsonDalle2() =
        getTextFromFile("$DIR_JSON/DallE2.json")

    fun getJsonDallE2Variations() =
        getTextFromFile("$DIR_JSON/DallE2Variations.json")

    fun getJsonStableDiffusion() =
        getTextFromFile("$DIR_JSON/StableDiffusion.json")

    fun getJsonTextToSpeech() =
        getTextFromFile("$DIR_JSON/TextToSpeech.json")

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
