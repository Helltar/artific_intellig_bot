package com.helltar.artific_intellig_bot.utils

import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object Utils {

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

    fun getFirstRegexGroup(text: String, regex: String): String {
        val m = Pattern.compile(regex).matcher(text)

        return if (m.find()) {
            if (m.groupCount() >= 1) m.group(1) else ""
        } else ""
    }

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
