package com.helltar.aibot.commands.user.audio

object MicrosoftTTS {

    data class VoiceData(
        val name: String,
        val styles: Set<String>? = null
    )

    const val DEFAULT_LOCALE = "en-US"
    const val DEFAULT_STYLE = "default"

    private val jenny =
        VoiceData(
            "JennyNeural",
            setOf(
                "assistant",
                "chat",
                "customerservice",
                "newscast",
                "angry",
                "cheerful",
                "sad",
                "excited",
                "friendly",
                "terrified",
                "shouting",
                "unfriendly",
                "whispering",
                "hopeful"
            )
        )

    private val polina = VoiceData("PolinaNeural")
    private val svetlana = VoiceData("SvetlanaNeural")
    private val agnieszka = VoiceData("AgnieszkaNeural")
    private val nanami = VoiceData("NanamiNeural", setOf("chat", "cheerful"))
    private val katja = VoiceData("KatjaNeural")

    val voices =
        mapOf(
            "en-US" to jenny,
            "uk-UA" to polina,
            "pl-PL" to agnieszka,
            "ja-JP" to nanami,
            "ru-RU" to svetlana,
            "de-DE" to katja
        )
}