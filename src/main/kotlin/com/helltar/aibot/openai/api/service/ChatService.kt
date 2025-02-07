package com.helltar.aibot.openai.api.service

import com.helltar.aibot.openai.api.ApiConfig.CHAT_MODEL
import com.helltar.aibot.openai.api.ApiConfig.COMPLETIONS_API_URL
import com.helltar.aibot.openai.api.ApiConfig.SPEECH_API_URL
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.Serialization.encodeToJsonString
import com.helltar.aibot.openai.api.Serialization.jsonDecodeFromString
import com.helltar.aibot.openai.api.models.audio.SpeechRequest
import com.helltar.aibot.openai.api.models.chat.ChatRequestData
import com.helltar.aibot.openai.api.models.chat.ChatResponseData
import com.helltar.aibot.openai.api.models.common.MessageData

class ChatService(private val apiClient: OpenAiClient) {

    fun getReply(messages: List<MessageData>): String {
        val body = encodeToJsonString(ChatRequestData(CHAT_MODEL, messages))
        val json = apiClient.postAsString(COMPLETIONS_API_URL, body)
        val response: ChatResponseData = jsonDecodeFromString(json)
        return response.choices.first().message.content
    }

    fun textToSpeech(input: String): ByteArray {
        val body = encodeToJsonString(SpeechRequest(input = input))
        return apiClient.postAsByteArray(SPEECH_API_URL, body)
    }
}
