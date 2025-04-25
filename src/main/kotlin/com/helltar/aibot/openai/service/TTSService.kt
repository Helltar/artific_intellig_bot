package com.helltar.aibot.openai.service

import com.helltar.aibot.openai.ApiClient
import com.helltar.aibot.openai.ApiConfig.Endpoints
import com.helltar.aibot.openai.Serialization.encodeToJsonString
import com.helltar.aibot.openai.models.audio.SpeechRequest

class TTSService(private val apiClient: ApiClient, private val model: String) {

    fun textToSpeech(input: String, voice: String = "nova", responseFormat: String = "opus"): ByteArray {
        val body = encodeToJsonString(SpeechRequest(model, input, voice, responseFormat))
        return apiClient.postAsByteArray(Endpoints.Audio.SPEECH, body)
    }
}
