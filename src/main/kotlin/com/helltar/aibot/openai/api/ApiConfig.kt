package com.helltar.aibot.openai.api

object ApiConfig {

    private const val BASE_URL = "https://api.openai.com/v1"

    const val COMPLETIONS_API_URL = "$BASE_URL/chat/completions"
    const val SPEECH_API_URL = "$BASE_URL/audio/speech"
    const val TRANSCRIPTIONS_API_URL = "$BASE_URL/audio/transcriptions"
    const val DALLE_VARIATIONS_API_URL = "$BASE_URL/images/variations"
    const val DALLE_GENERATIONS_API_URL = "$BASE_URL/images/generations"

    const val CHAT_MODEL = "gpt-4o"
    const val CHAT_ROLE_USER = "user"
    const val CHAT_ROLE_ASSISTANT = "assistant"
    const val CHAT_ROLE_SYSTEM = "system"

    const val DALLE_GENERATIONS_MODEL = "dall-e-3"
    const val DALLE_GENERATIONS_IMAGE_SIZE = "1024x1024"
    const val DALLE_VARIATIONS_IMAGE_SIZE = "256x256"

    const val MESSAGE_CONTENT_TYPE_TEXT = "text"
    const val MESSAGE_CONTENT_TYPE_IMAGE = "image_url"

    object DeepSeek {
        const val COMPLETIONS_API_URL = "https://api.deepseek.com/chat/completions"
        const val CHAT_MODEL = "deepseek-chat"
    }
}
