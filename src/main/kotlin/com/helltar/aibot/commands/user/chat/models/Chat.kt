package com.helltar.aibot.commands.user.chat.models

import kotlinx.serialization.Serializable

object Chat {

    /*
      https://platform.openai.com/docs/api-reference/chat/create
      https://api-docs.deepseek.com/api/create-chat-completion
    */

    const val OPENAI_CHAT_API_URL = "https://api.openai.com/v1/chat/completions"
    const val OPENAI_TTS_API_URL = "https://api.openai.com/v1/audio/speech"
    const val DEEPSEEK_CHAT_API_URL = "https://api.deepseek.com/chat/completions"

    const val CHAT_GPT_MODEL = "gpt-4o"
    const val DEEPSEEK_MODEL = "deepseek-chat"

    const val CHAT_ROLE_USER = "user"
    const val CHAT_ROLE_ASSISTANT = "assistant"
    const val CHAT_ROLE_DEVELOPER = "system" // developer

    @Serializable
    data class RequestData(
        val model: String = CHAT_GPT_MODEL,
        val messages: List<MessageData>
    )

    @Serializable
    data class ResponseData(
        val model: String,
        val choices: List<ChoiceData>,
        val usage: UsageData
    )

    @Serializable
    data class MessageData(
        val role: String,
        val content: String
    )

    @Serializable
    data class ChoiceData(
        val index: Int,
        val message: MessageData,
        val finish_reason: String
    )

    @Serializable
    data class UsageData(
        val prompt_tokens: Int,
        val completion_tokens: Int,
        val total_tokens: Int
    )

    /* https://platform.openai.com/docs/api-reference/audio/createSpeech */

    @Serializable
    data class SpeechRequestData(
        val model: String = "tts-1",
        val input: String,
        val voice: String = "nova",
        val response_format: String = "opus"
    )
}
