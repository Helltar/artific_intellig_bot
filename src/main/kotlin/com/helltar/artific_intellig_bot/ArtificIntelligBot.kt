package com.helltar.artific_intellig_bot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.BotModule
import com.annimon.tgbotsmodule.Runner
import com.annimon.tgbotsmodule.beans.Config
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.annimon.tgbotsmodule.services.YamlConfigLoaderService
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.helltar.artific_intellig_bot.db.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

private val requestList = hashMapOf<String, Job>()

class ArtificIntelligBot : BotModule {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            init()
            Runner.run("", listOf(ArtificIntelligBot()))
        }

        private fun init() {
            val databaseDir = File(DIR_DB)

            if (!databaseDir.exists() && !databaseDir.mkdir())
                throw RuntimeException("Error when create dir: $DIR_DB")

            Database.init()

            LoggerFactory.getLogger(ArtificIntelligBot::class.java).info("start ...")
        }

        fun addRequest(requestKey: String, ctx: MessageContext, func: () -> Unit) {
            if (requestList.containsKey(requestKey))
                if (requestList[requestKey]?.isCompleted == false) {
                    ctx.replyToMessage()
                        .setText(Strings.many_request)
                        .callAsync(ctx.sender)

                    return
                }

            requestList[requestKey] = CoroutineScope(Dispatchers.Default).launch { func() }
        }
    }

    override fun botHandler(config: Config): BotHandler {
        val configLoader = YamlConfigLoaderService()
        val configFile = configLoader.configFile(FILE_BOT_CONFIG, config.profile)
        val botConfig = configLoader.loadFile(configFile, BotMainConfig::class.java) {
            it.registerModule(KotlinModule.Builder().build())
        }

        Database.sudoers.add(botConfig.creatorId, "Owner")

        return ArtificIntelligBotHandler(botConfig)
    }
}
