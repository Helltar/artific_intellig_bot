package com.helltar.aibot.commands.user.image

import com.annimon.tgbotsmodule.commands.context.MessageContext
import com.helltar.aibot.commands.Commands
import com.helltar.aibot.config.Strings
import com.helltar.aibot.openai.api.OpenAiClient
import com.helltar.aibot.openai.api.service.VisionService
import io.github.oshai.kotlinlogging.KotlinLogging

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
            val answer = VisionService(OpenAiClient(openaiKey())).analyzeImage(argumentsString, photo)

            try {
                replyToMessage(answer, markdown = true)
            } catch (e: Exception) {
                log.error { e.message }
                errorReplyWithTextDocument(answer, Strings.TELEGRAM_API_EXCEPTION_RESPONSE_SAVED_TO_FILE)
            }
        } catch (e: Exception) {
            log.error { e.message }
            replyToMessage(Strings.CHAT_EXCEPTION)
        } finally {
            photo.delete()
        }
    }

    override fun getCommandName() =
        Commands.User.CMD_GPT_VISION
}
