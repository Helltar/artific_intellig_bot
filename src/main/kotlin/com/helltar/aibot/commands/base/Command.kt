package com.helltar.aibot.commands.base

interface Command {

    suspend fun run()
    fun getCommandName(): String
}
