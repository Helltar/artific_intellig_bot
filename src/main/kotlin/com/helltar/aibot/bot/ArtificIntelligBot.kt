package com.helltar.aibot.bot

import com.annimon.tgbotsmodule.BotModule
import com.annimon.tgbotsmodule.BotModuleOptions
import com.annimon.tgbotsmodule.Runner
import com.annimon.tgbotsmodule.beans.Config
import com.helltar.aibot.Config.telegramBotToken
import com.helltar.aibot.database.Database
import com.helltar.aibot.database.dao.apiKeyDao
import com.helltar.aibot.openai.ApiClient
import com.helltar.aibot.openai.ApiConfig
import kotlinx.coroutines.runBlocking

class ArtificIntelligBot : BotModule {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Runner.run("", listOf(ArtificIntelligBot()))
        }
    }

    init {
        Database.init()

        runBlocking {
            ApiClient.configure(apiKeyDao.getKey(ApiConfig.PROVIDER_NAME))
        }
    }

    override fun botHandler(config: Config) =
        ArtificIntelligBotHandler(BotModuleOptions.createDefault(telegramBotToken))
}
