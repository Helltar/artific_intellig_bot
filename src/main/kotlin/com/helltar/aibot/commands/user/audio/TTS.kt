package com.helltar.aibot.commands.user.audio

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.BotCommand
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.chat.models.Chat
import com.helltar.aibot.utils.NetworkUtils
import com.helltar.aibot.utils.NetworkUtils.postJson
import kotlinx.serialization.encodeToString
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream

class TTS(ctx: MessageContext) : BotCommand(ctx) {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun run() {
        val style = if (arguments.size > 1) arguments.last() else arguments.firstOrNull() ?: MicrosoftTTS.DEFAULT_STYLE
        val text = if (isReply) replyMessage?.text ?: replyMessage?.caption else argumentsString

        if (text.isNullOrBlank()) {
            replyToMessage(Strings.MESSAGE_TEXT_NOT_FOUND)
            return
        }

        val locale = detectLanguage(text).takeIf { it in MicrosoftTTS.voices.keys } ?: MicrosoftTTS.DEFAULT_LOCALE
        val response = sendPrompt(text, locale, style)

        if (response.isSuccessful) {
            try {
                val messageId = if (isReply) replyMessage!!.messageId else message.messageId
                sendVoice("tts-$messageId", ByteArrayInputStream(response.data), messageId)
            } catch (e: Exception) {
                log.error(e.message)
            }
        } else
            log.error("microsoft speech status сode: ${response.statusCode}")
    }

    override fun getCommandName() =
        Commands.CMD_TTS

    private suspend fun sendPrompt(prompt: String, locale: String, voiceStyle: String): Response {
        val url = "https://northeurope.tts.speech.microsoft.com/cognitiveservices/v1"

        val headers =
            mapOf(
                "Ocp-Apim-Subscription-Key" to "${getApiKey(PROVIDER_MICROSOFT)}",
                "Content-Type" to "application/ssml+xml",
                "X-Microsoft-OutputFormat" to "audio-48khz-96kbitrate-mono-mp3",
                "User-Agent" to "aibot"
            )

        val model = MicrosoftTTS.voices.getValue(locale)
        val voice = "$locale-${model.name}"
        val style = voiceStyle.takeIf { model.styles?.contains(it) == true } ?: MicrosoftTTS.DEFAULT_STYLE
        val gender = "Female"
        val escapedPrompt = prompt.removeSuffix(style).escapeHtml()

        val body =
            """
               <speak version='1.0' xml:lang='$locale' xmlns='http://www.w3.org/2001/10/synthesis' xmlns:mstts='http://www.w3.org/2001/mstts'>
                 <voice gender='$gender' name='$voice'>
                   <mstts:express-as style='$style'>$escapedPrompt</mstts:express-as>
                 </voice>
               </speak>
            """
                .trimIndent()

        return NetworkUtils.post(url, headers, body)
    }

    /* todo: find a better way */

    private suspend fun detectLanguage(text: String): String? {
        val prompt = "Write a locale for the language used in this text, answer simply en-US, etc., nothing extra, text:"
        val textSnippet = if (text.length > 100) text.substring(0, 100) else text
        val message = Chat.MessageData(Chat.CHAT_ROLE_USER, "$prompt\n\n\"$textSnippet\"")

        val url = "https://api.openai.com/v1/chat/completions"
        val body = json.encodeToString(Chat.RequestData(Chat.CHAT_GPT_MODEL_4_MINI, listOf(message)))
        val response = postJson(url, getOpenAIHeaders(), body)

        return if (response.isSuccessful) {
            try {
                json.decodeFromString<Chat.ResponseData>(response.data.decodeToString()).choices.first().message.content
            } catch (e: Exception) {
                log.error(e.message)
                null
            }
        } else {
            log.error("$response")
            null
        }
    }

    private fun String.escapeHtml() =
        this
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#039;")
}