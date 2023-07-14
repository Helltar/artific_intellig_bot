package com.helltar.artific_intellig_bot.commands.user.chat

object ChatGPTData {

    const val CHAT_GPT_MODEL = "gpt-3.5-turbo"
    const val CHAT_ROLE_USER = "user"
    const val CHAT_ROLE_ASSISTANT = "assistant"
    const val CHAT_ROLE_SYSTEM = "system"

    data class ChatData(
        val model: String,
        val messages: List<ChatMessageData>
    )

    data class ChatMessageData(
        val role: String,
        val content: String
    )

    /* https://cloud.google.com/text-to-speech/docs/reference/rest/v1/text/synthesize */

    data class TtsInputData(
        val text: String
    )

    data class TtsVoiceData(
        val languageCode: String,
        val ssmlGender: String = "FEMALE"
    )

    data class TtsAudioConfigData(
        val audioEncoding: String = "OGG_OPUS",
        val speakingRate: Float = 1.0f
    )

    data class TextToSpeechJsonData(
        val input: TtsInputData,
        val voice: TtsVoiceData,
        val audioConfig: TtsAudioConfigData
    )
}