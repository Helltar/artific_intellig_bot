package com.helltar.artific_intellig_bot

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.management.ManagementFactory
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object Utils {

    private val log = LoggerFactory.getLogger(javaClass)

    fun randomUUID() = UUID.randomUUID().toString()

    fun runProcess(command: String, workDir: File? = null) =
        try {
            ProcessBuilder(command.split(" "))
                .directory(workDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start().run {
                    waitFor(30, TimeUnit.SECONDS)
                    inputStream.bufferedReader().readText()
                }
        } catch (e: Exception) {
            log.error(e.message, e)
            ""
        }

    fun getTextFromFile(filename: String): String =
        try {
            File(filename).run { if (!exists()) createNewFile() }
            FileReader(filename).readText()
        } catch (e: IOException) {
            log.error(e.message, e)
            ""
        }

    fun detectLangCode(text: String): String {

        fun find(regex: String) = Pattern.compile(regex).matcher(text).find()

        if (find("[ЇїІіЄєҐґ]"))
            return "uk-UA"

        if (find("[ёЁэЭъЪыЫ]"))
            return "ru-RU"

        if (find("\\w"))
            return "en-US"

        return "uk-UA"
    }

    fun getSysStat() =
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
        ManagementFactory.getMemoryMXBean().heapMemoryUsage.run {
            "Memory: ${used / (1024 * 1024)} / ${max / (1024 * 1024)}"
        }
}
