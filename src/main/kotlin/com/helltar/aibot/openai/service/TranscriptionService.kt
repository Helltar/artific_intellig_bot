package com.helltar.aibot.openai.service

import com.github.kittinunf.fuel.core.FileDataPart
import com.helltar.aibot.openai.ApiClient
import com.helltar.aibot.openai.ApiConfig.Endpoints
import com.helltar.aibot.openai.Serialization.jsonDecodeFromString
import com.helltar.aibot.openai.models.audio.TranscriptionResponse
import java.io.File

class TranscriptionService(private val apiClient: ApiClient, private val model: String) {

    /* https://platform.openai.com/docs/api-reference/audio/createTranscription */

    fun transcribeAudio(file: File): String {
        val parameters = listOf("model" to model)
        val dataPart = FileDataPart(file, "file")
        val json = apiClient.uploadWithFile(Endpoints.Audio.TRANSCRIPTIONS, parameters, dataPart)
        val response: TranscriptionResponse = jsonDecodeFromString(json)
        return response.text
    }
}
