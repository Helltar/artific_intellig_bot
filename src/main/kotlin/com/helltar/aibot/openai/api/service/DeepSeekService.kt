package com.helltar.aibot.openai.api.service

import com.helltar.aibot.openai.api.ApiConfig.DeepSeek
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.Serialization.json
import com.helltar.aibot.openai.api.models.chat.ChatRequestData
import com.helltar.aibot.openai.api.models.chat.ChatResponseData
import com.helltar.aibot.openai.api.models.common.MessageData
import kotlinx.serialization.encodeToString

class DeepSeekService(private val apiClient: OpenAiClient) {

    fun getReply(messages: List<MessageData>): String {
        val body = json.encodeToString(ChatRequestData(DeepSeek.CHAT_MODEL, messages))
        val responseJson = apiClient.postAsString(DeepSeek.COMPLETIONS_API_URL, body)
        return json.decodeFromString<ChatResponseData>(responseJson).choices.first().message.content
    }
}
