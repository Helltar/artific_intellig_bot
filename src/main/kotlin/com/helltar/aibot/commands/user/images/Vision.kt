package com.helltar.aibot.commands.user.images

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.Strings
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.commands.user.chat.models.Chat.OPENAI_CHAT_API_URL
import com.helltar.aibot.commands.user.images.models.Dalle
import com.helltar.aibot.commands.user.images.models.Vision
import com.helltar.aibot.utils.Network.postAsString
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

        if (replyMessage?.hasPhoto() == false) {
            replyToMessage(Strings.GPT_VISION_NO_PHOTO_IN_MESSAGE)
            return
        }

        val photo = downloadPhotoOrReplyIfInvalid() ?: return

        try {
            val responseJson = sendPrompt(argumentsString, photo).also { log.debug { it } }
            val answer = json.decodeFromString<Vision.ResponseData>(responseJson).choices.first().message.content

            try {
                replyToMessage(answer, markdown = true)
            } catch (e: Exception) {
                log.error { e.message }
                errorReplyWithTextDocument(answer, Strings.TELEGRAM_API_EXCEPTION_RESPONSE_SAVED_TO_FILE)
            }
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.CHAT_EXCEPTION)
        }
    }

    override fun getCommandName() =
        Commands.CMD_GPT_VISION

    private suspend fun sendPrompt(text: String, image: File): String {
        val imageBase64 = Base64.getEncoder().encodeToString(image.readBytes())
        val imageData = Dalle.ImageData("data:image/jpeg;base64,$imageBase64")
        val contentTextData = Vision.ContentData(Vision.MESSAGE_CONTENT_TYPE_TEXT, text)
        val contentImageData = Vision.ContentData(Vision.MESSAGE_CONTENT_TYPE_IMAGE, image_url = imageData)
        val requestData = Vision.RequestData(messages = listOf(Vision.MessageData(content = listOf(contentTextData, contentImageData))))
        return postAsString(OPENAI_CHAT_API_URL, createOpenAIHeaders(), json.encodeToString(requestData))
    }
}
