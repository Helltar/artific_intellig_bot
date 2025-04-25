package com.helltar.aibot.openai

object ApiConfig {

    object OpenAi {
        const val PROVIDER_NAME = "openai.com"
        const val BASE_URL = "https://api.openai.com/v1"
    }

    object Endpoints {
        const val CHAT_COMPLETIONS = "/chat/completions"

        object Audio {
            const val SPEECH = "/audio/speech"
            const val TRANSCRIPTIONS = "/audio/transcriptions"
        }

        object Images {
            const val VARIATIONS = "/images/variations"
            const val GENERATIONS = "/images/generations"
        }
    }

    object ChatRole {
        const val USER = "user"
        const val ASSISTANT = "assistant"
        const val SYSTEM = "system"
    }

    object ChatContentType {
        const val TEXT = "text"
        const val IMAGE_URL = "image_url"
    }

    object Models {
        const val CHAT = "gpt-4.1"
        const val TTS = "gpt-4o-mini-tts"
        const val VISION = "gpt-4.1"
        const val TRANSCRIPTION = "gpt-4o-transcribe"
        const val IMAGE_GENERATION = "dall-e-3"
    }
}
