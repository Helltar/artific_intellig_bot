package com.helltar.artific_intellig_bot

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

object Utils {

    private val log = LoggerFactory.getLogger(javaClass)

    fun randomUUID() = UUID.randomUUID().toString()

    fun getLineFromFile(filename: String): String =
        try {
            BufferedReader(FileReader(filename)).readLine()
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

    fun getListFromFile(filename: String) =
        getTextFromFile(filename).run {
            if (this.isNotEmpty())
                split("\n")
            else
                listOf()
        }

    fun escapeHtml(text: String) = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#039;")

    fun detectLangCode(text: String): String {

        fun find(regex: String) = Pattern.compile(regex).matcher(text).find()

        if (find("[ёЁэЭъЪыЫ]"))
            return "ru-RU"

        if (find("[ЇїІіЄєҐґ]"))
            return "uk-UA"

        if (find("\\w"))
            return "en-US"

        return "uk-UA"
    }
}
