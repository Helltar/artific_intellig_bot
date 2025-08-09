package com.helltar.aibot.openai.service

import com.helltar.aibot.openai.ApiConfig.Endpoints
import com.helltar.aibot.openai.HttpClient
import com.helltar.aibot.openai.models.chat.ChatRequestData
import com.helltar.aibot.openai.models.chat.ChatResponseData
import com.helltar.aibot.openai.models.common.MessageData

class ChatService(private val client: HttpClient = HttpClient, private val model: String, private val apiKey: String) {

    suspend fun getReply(messages: List<MessageData>): String {
        val request = ChatRequestData(model, messages)
        val response: ChatResponseData = client.post(apiKey, Endpoints.CHAT_COMPLETIONS, request)
        return response.choices.first().message.content
    }
}
