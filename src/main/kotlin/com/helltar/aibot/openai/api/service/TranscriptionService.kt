package com.helltar.aibot.openai.api.service

import com.github.kittinunf.fuel.core.FileDataPart
import com.helltar.aibot.openai.api.ApiConfig.TRANSCRIPTIONS_API_URL
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.Serialization.jsonDecodeFromString
import com.helltar.aibot.openai.api.models.audio.TranscriptionResponse
import java.io.File

class TranscriptionService(private val apiClient: OpenAiClient) {

    /* https://platform.openai.com/docs/api-reference/audio/createTranscription */

    fun transcribeAudio(file: File): String {
        val parameters = listOf("model" to "whisper-1")
        val dataPart = FileDataPart(file, "file")
        val json = apiClient.uploadWithFile(TRANSCRIPTIONS_API_URL, parameters, dataPart)
        val response: TranscriptionResponse = jsonDecodeFromString(json)
        return response.text
    }
}
