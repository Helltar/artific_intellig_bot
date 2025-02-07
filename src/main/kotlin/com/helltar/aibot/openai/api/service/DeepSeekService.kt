package com.helltar.aibot.openai.api.service

import com.helltar.aibot.openai.api.ApiConfig.DeepSeek
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.Serialization.encodeToJsonString
import com.helltar.aibot.openai.api.Serialization.jsonDecodeFromString
import com.helltar.aibot.openai.api.models.chat.ChatRequestData
import com.helltar.aibot.openai.api.models.chat.ChatResponseData
import com.helltar.aibot.openai.api.models.common.MessageData

class DeepSeekService(private val apiClient: OpenAiClient) {

    fun getReply(messages: List<MessageData>): String {
        val body = encodeToJsonString(ChatRequestData(DeepSeek.CHAT_MODEL, messages))
        val json = apiClient.postAsString(DeepSeek.COMPLETIONS_API_URL, body)
        val response: ChatResponseData = jsonDecodeFromString(json)
        return response.choices.first().message.content
    }
}
