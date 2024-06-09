package com.helltar.aibot.commands.user

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

class Uptime(ctx: MessageContext) : BotCommand(ctx) {

    override suspend fun run() {
        replyToMessage(getSysStat())
    }

    override fun getCommandName() =
        Commands.CMD_UPTIME

    private fun getSysStat() =
        "<code>Threads: ${ManagementFactory.getThreadMXBean().threadCount}\n${getMemUsage()}\n${getJVMUptime()}</code>"

    private fun getJVMUptime() =
        ManagementFactory.getRuntimeMXBean().run {
            TimeUnit.MILLISECONDS.run {
                "Uptime: " +
                        "${toDays(uptime)} d. " +
                        "${toHours(uptime) % 24} h. " +
                        "${toMinutes(uptime) % 60} m. " +
                        "${toSeconds(uptime) % 60} s."
            }
        }

    private fun getMemUsage() =
        ManagementFactory.getMemoryMXBean().heapMemoryUsage.run { "Memory: ${used / (1024 * 1024)} / ${max / (1024 * 1024)}" }
}