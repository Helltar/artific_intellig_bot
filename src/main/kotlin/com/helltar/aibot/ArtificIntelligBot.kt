package com.helltar.aibot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.BotModule
import com.annimon.tgbotsmodule.Runner
import com.annimon.tgbotsmodule.beans.Config
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.annimon.tgbotsmodule.services.YamlConfigLoaderService
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.helltar.aibot.BotConfig.DIR_DB
import com.helltar.aibot.BotConfig.FILE_BOT_CONFIG
import com.helltar.aibot.commands.Commands.CMD_CHAT_AS_TEXT
import com.helltar.aibot.commands.Commands.CMD_CHAT_AS_VOICE
import com.helltar.aibot.commands.Commands.disalableCommandsList
import com.helltar.aibot.dao.DatabaseFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

class ArtificIntelligBot : BotModule {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private val requestList = hashMapOf<String, Job>()

        @JvmStatic
        fun main(args: Array<String>) {
            init()
            Runner.run("", listOf(ArtificIntelligBot()))
        }

        private fun init() {
            val databaseDir = File(DIR_DB)

            if (!databaseDir.exists() && !databaseDir.mkdir())
                throw RuntimeException("Error when create dir: $DIR_DB")

            DatabaseFactory.init()

            // todo: DatabaseFactory.commandsState.add
            disalableCommandsList.forEach { command -> DatabaseFactory.commandsState.add(command) }
            DatabaseFactory.commandsState.add(CMD_CHAT_AS_TEXT)
            DatabaseFactory.commandsState.add(CMD_CHAT_AS_VOICE, true)
        }

        fun addRequest(requestKey: String, ctx: MessageContext, func: () -> Unit) {
            if (requestList.containsKey(requestKey))
                if (requestList[requestKey]?.isCompleted == false) {
                    ctx.replyToMessage()
                        .setText(Strings.MANY_REQUEST)
                        .callAsync(ctx.sender)

                    return
                }

            requestList[requestKey] = CoroutineScope(Dispatchers.IO).launch { func() }
        }
    }

    override fun botHandler(config: Config): BotHandler {
        val configLoader = YamlConfigLoaderService()
        val configFile = configLoader.configFile(FILE_BOT_CONFIG, config.profile)

        val botConfig =
            configLoader.loadFile(configFile, BotConfig.JsonData::class.java) {
                it.registerModule(KotlinModule.Builder().build())
            }

        DatabaseFactory.sudoers.add(botConfig.creatorId, "Owner")
        log.info("start ...")

        return ArtificIntelligBotHandler(botConfig)
    }
}