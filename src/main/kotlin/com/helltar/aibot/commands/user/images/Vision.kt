package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.images.models.Dalle
import com.helltar.aibot.commands.user.images.models.Vision
import com.helltar.aibot.utils.Network.postJson
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import java.io.File
import java.util.*

private val log = KotlinLogging.logger {}

class Vision(ctx: MessageContext) : DallEVariations(ctx) {

    override suspend fun run() {
        if (isNotReply) {
            replyToMessage(Strings.GPT_VISION_USE_AS_REPLY)
            return
        }

        if (!replyMessage!!.hasPhoto()) {
            replyToMessage(Strings.GPT_VISION_NO_PHOTO_IN_MESSAGE)
            return
        }

        val photo = downloadPhotoIfValid() ?: return

        val response = sendPrompt(argumentsString, photo)
        val responseJson = response.data.decodeToString()

        if (response.isSuccessful) {
            val answer =
                try {
                    json.decodeFromString<Vision.ResponseData>(responseJson).choices.first().message.content
                } catch (e: Exception) {
                    log.error { "${e.message} $responseJson" }
                    replyToMessage(Strings.CHAT_EXCEPTION)
                    return
                }

            try {
                replyToMessage(answer, markdown = true)
            } catch (e: Exception) {
                errorReplyWithTextDocument(answer, Strings.TELEGRAM_API_EXCEPTION_RESPONSE_SAVED_TO_FILE)
                log.error { e.message }
            }
        } else {
            replyToMessage(Strings.CHAT_EXCEPTION)
            log.error { responseJson }
        }
    }

    override fun getCommandName() =
        Commands.CMD_GPT_VISION

    private suspend fun sendPrompt(text: String, image: File): Response {
        val url = "https://api.openai.com/v1/chat/completions"

        val imageBase64 = Base64.getEncoder().encodeToString(image.readBytes())
        val imageData = Dalle.ImageData("data:image/jpeg;base64,$imageBase64")
        val contentTextData = Vision.ContentData(Vision.MESSAGE_CONTENT_TYPE_TEXT, text)
        val contentImageData = Vision.ContentData(Vision.MESSAGE_CONTENT_TYPE_IMAGE, image_url = imageData)
        val requestData = Vision.RequestData(messages = listOf(Vision.MessageData(content = listOf(contentTextData, contentImageData))))

        return postJson(url, createOpenAIHeaders(), json.encodeToString(requestData))
    }
}
