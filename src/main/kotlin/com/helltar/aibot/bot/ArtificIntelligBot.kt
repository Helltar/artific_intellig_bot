package com.helltar.aibot.bot

import com.annimon.tgbotsmodule.BotModule
import com.annimon.tgbotsmodule.BotModuleOptions
import com.annimon.tgbotsmodule.Runner
import com.annimon.tgbotsmodule.beans.Config
import com.helltar.aibot.Config.telegramBotToken
import com.helltar.aibot.database.Database

class ArtificIntelligBot : BotModule {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Database.init()
            Runner.run("", listOf(ArtificIntelligBot()))
        }
    }

    override fun botHandler(config: Config) =
        ArtificIntelligBotHandler(BotModuleOptions.createDefault(telegramBotToken))
}
