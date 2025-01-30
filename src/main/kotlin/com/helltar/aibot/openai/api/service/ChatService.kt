package com.helltar.aibot.openai.api.service

import com.helltar.aibot.openai.api.ApiConfig.CHAT_MODEL
import com.helltar.aibot.openai.api.ApiConfig.COMPLETIONS_API_URL
import com.helltar.aibot.openai.api.ApiConfig.SPEECH_API_URL
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.Serialization.json
import com.helltar.aibot.openai.api.models.audio.SpeechRequest
import com.helltar.aibot.openai.api.models.chat.ChatRequestData
import com.helltar.aibot.openai.api.models.chat.ChatResponseData
import com.helltar.aibot.openai.api.models.common.MessageData
import kotlinx.serialization.encodeToString

class ChatService(private val apiClient: OpenAiClient) {

    fun getReply(messages: List<MessageData>): String {
        val body = json.encodeToString(ChatRequestData(CHAT_MODEL, messages))
        val responseJson = apiClient.postAsString(COMPLETIONS_API_URL, body)
        return json.decodeFromString<ChatResponseData>(responseJson).choices.first().message.content
    }

    fun textToSpeech(input: String): ByteArray {
        val body = json.encodeToString(SpeechRequest(input = input))
        return apiClient.postAsByteArray(SPEECH_API_URL, body)
    }
}
