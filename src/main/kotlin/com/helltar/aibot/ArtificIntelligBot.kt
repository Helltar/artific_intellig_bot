package com.helltar.aibot

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.BotModule
import com.annimon.tgbotsmodule.Runner
import com.annimon.tgbotsmodule.beans.Config
import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.annimon.tgbotsmodule.services.YamlConfigLoaderService
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.helltar.aibot.Commands.cmdChatAsText
import com.helltar.aibot.Commands.cmdChatAsVoice
import com.helltar.aibot.Commands.disalableCmdsList
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
            disalableCmdsList.forEach { command -> DatabaseFactory.commandsState.add(command) }
            DatabaseFactory.commandsState.add(cmdChatAsText)
            DatabaseFactory.commandsState.add(cmdChatAsVoice, true)
        }

        fun addRequest(requestKey: String, ctx: MessageContext, func: () -> Unit) {
            if (requestList.containsKey(requestKey))
                if (requestList[requestKey]?.isCompleted == false) {
                    ctx.replyToMessage()
                        .setText(Strings.many_request)
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
            configLoader.loadFile(configFile, BotMainConfig::class.java) {
                it.registerModule(KotlinModule.Builder().build())
            }

        DatabaseFactory.sudoers.add(botConfig.creatorId, "Owner")
        log.info("start ...")

        return ArtificIntelligBotHandler(botConfig)
    }
}