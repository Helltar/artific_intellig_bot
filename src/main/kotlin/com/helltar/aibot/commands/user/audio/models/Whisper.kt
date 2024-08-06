package com.helltar.aibot.commands.user.audio.models

import kotlinx.serialization.Serializable

object Whisper {

    @Serializable
    data class ResponseData(val text: String)
}