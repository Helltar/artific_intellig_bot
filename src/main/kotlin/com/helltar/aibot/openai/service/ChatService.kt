package com.helltar.aibot.openai.service

import com.helltar.aibot.openai.ApiClient
import com.helltar.aibot.openai.ApiConfig.Endpoints
import com.helltar.aibot.openai.Serialization.encodeToJsonString
import com.helltar.aibot.openai.Serialization.jsonDecodeFromString
import com.helltar.aibot.openai.models.chat.ChatRequestData
import com.helltar.aibot.openai.models.chat.ChatResponseData
import com.helltar.aibot.openai.models.common.MessageData

class ChatService(private val apiClient: ApiClient, private val model: String) {

    fun getReply(messages: List<MessageData>): String {
        val body = encodeToJsonString(ChatRequestData(model, messages))
        val json = apiClient.postAsString(Endpoints.CHAT_COMPLETIONS, body)
        val response: ChatResponseData = jsonDecodeFromString(json)
        return response.choices.first().message.content
    }
}
