package com.helltar.artific_intellig_bot

import com.helltar.artific_intellig_bot.Utils.getLineFromFile
import com.helltar.artific_intellig_bot.Utils.getListFromFile
import com.helltar.artific_intellig_bot.Utils.getTextFromFile

open class BotConfig {

    companion object {
        private const val DIR_CONFIG = "config"
        private const val DIR_JSON = "$DIR_CONFIG/json"

        const val DIR_DB = "db/"
        const val DIR_STABLE_DIFFUSION = "stable_diffusion/"
        const val DIR_TEXT_TO_SPEECH = "text_to_speech/"
        const val EXT_DISABLED = ".disabled"

        val BOT_USERNAME = getLineFromFile("$DIR_CONFIG/bot_username.txt")
        val BOT_TOKEN = getLineFromFile("$DIR_CONFIG/bot_token.txt")
    }

    val chatsWhiteList = getListFromFile("$DIR_CONFIG/chats_white_list.txt")
    val sudoers = getListFromFile("$DIR_CONFIG/sudoers.txt")

    val openaiToken = getLineFromFile("$DIR_CONFIG/openai_token.txt")
    val stableDiffusionToken = getLineFromFile("$DIR_CONFIG/stable_diffusion_token.txt")
    val textToSpeechToken = getLineFromFile("$DIR_CONFIG/text_to_speech_token.txt")

    val jsonChatGPT = getTextFromFile("$DIR_JSON/ChatGPT.json")
    val jsonDalle2 = getTextFromFile("$DIR_JSON/DallE2.json")
    val jsonStableDiffusion = getTextFromFile("$DIR_JSON/StableDiffusion.json")
    val jsonTextToSpeech = getTextFromFile("$DIR_JSON/TextToSpeech.json")
}
