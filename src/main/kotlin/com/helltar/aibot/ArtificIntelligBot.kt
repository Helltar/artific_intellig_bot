package com.helltar.aibot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.BotModule
import com.annimon.tgbotsmodule.Runner
import com.annimon.tgbotsmodule.beans.Config
import com.annimon.tgbotsmodule.services.YamlConfigLoaderService
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.helltar.aibot.BotConfig.FILE_BOT_CONFIG
import com.helltar.aibot.dao.DatabaseFactory
import org.slf4j.LoggerFactory

class ArtificIntelligBot : BotModule {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Runner.run("", listOf(ArtificIntelligBot()))
        }
    }

    override fun botHandler(config: Config): BotHandler {
        val configLoader = YamlConfigLoaderService()
        val configFile = configLoader.configFile(FILE_BOT_CONFIG, config.profile)

        val botConfig =
            configLoader.loadFile(configFile, BotConfig.JsonData::class.java) {
                it.registerModule(KotlinModule.Builder().build())
            }

        DatabaseFactory.init(botConfig.creatorId)

        log.info("start ...")

        return ArtificIntelligBotHandler(botConfig)
    }
}