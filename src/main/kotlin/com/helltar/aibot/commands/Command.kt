package com.helltar.aibot.commands

interface Command {

    suspend fun run()
    fun getCommandName(): String
}
